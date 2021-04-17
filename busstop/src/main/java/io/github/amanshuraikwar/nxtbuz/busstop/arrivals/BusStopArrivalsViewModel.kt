package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalsScreenState
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.model.busroute.BusRouteNavigationParams
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.StopBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleStarUpdateUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named

class BusStopArrivalsViewModel @Inject constructor(
    private val getBusArrivalFlowUseCase: GetBusArrivalFlowUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
    private val stopBusArrivalFlowUseCase: StopBusArrivalFlowUseCase,
    private val toggleStar: ToggleBusStopStarUseCase,
    private val toggleStarUpdateUseCase: ToggleStarUpdateUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private var busStop: BusStop? = null
    private var listItems = SnapshotStateList<BusStopArrivalListItemData>()

    private val _screenState =
        MutableStateFlow<BusStopArrivalsScreenState>(BusStopArrivalsScreenState.Fetching)
    val screenState: StateFlow<BusStopArrivalsScreenState> = _screenState

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(Error())
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation
    private val busArrivalListLock = Mutex()
    internal var bottomSheetInit = false

    fun init(busStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            var busStop = this@BusStopArrivalsViewModel.busStop

            if (busStopCode == busStop?.code) {
                busArrivalListLock.withLock {
                    listItems = SnapshotStateList()
                }

                _screenState.emit(
                        BusStopArrivalsScreenState.Success(
                                BusStopArrivalListItemData.BusStopHeader(
                                        busStopCode = busStop.code,
                                        busStopDescription = busStop.description,
                                        busStopRoadName = busStop.roadName,
                                ),
                                listItems
                        )
                )
            } else {
                _screenState.emit(BusStopArrivalsScreenState.Fetching)

                busStop = getBusStopUseCase(busStopCode)

                busArrivalListLock.withLock {
                    listItems = SnapshotStateList()
                }

                _screenState.emit(
                        BusStopArrivalsScreenState.Success(
                                BusStopArrivalListItemData.BusStopHeader(
                                        busStopCode = busStop.code,
                                        busStopDescription = busStop.description,
                                        busStopRoadName = busStop.roadName,
                                ),
                                listItems
                        )
                )
            }

            busArrivalListLock.withLock {
                addBusStopMapMarker(busStop = busStop)
                this@BusStopArrivalsViewModel.busStop = busStop
            }
            
            listenToggleStarUpdate()
            waitForBottomSheetInit()
            startListeningArrivals()
        }
    }

    private suspend fun waitForBottomSheetInit() {
        while (true) {
            delay(300)
            if (bottomSheetInit) break
        }
    }

    private fun addBusStopMapMarker(busStop: BusStop) {
        pushMapEventUseCase(
            MapEvent.AddMarker(
                MapMarker(
                    busStop.code,
                    busStop.latitude,
                    busStop.longitude,
                    R.drawable.ic_marker_bus_stop_48,
                    busStop.description
                )
            )
        )
    }

    private fun listenToggleStarUpdate() {
        viewModelScope.launch(coroutineContext) {
            toggleStarUpdateUseCase()
                .collect { toggleStarUpdate ->
                    if (toggleStarUpdate.busStopCode == busStop?.code) {
                        busArrivalListLock.withLock {
                            val listItemIndex =
                                listItems.indexOfFirst {
                                    it is BusStopArrivalListItemData.BusStopArrival
                                            && it.busServiceNumber ==
                                            toggleStarUpdate.busServiceNumber
                                }

                            if (listItemIndex != -1) {
                                when (val listItem = listItems[listItemIndex]) {
                                    is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                                        listItems[listItemIndex] =
                                            listItem.copy(starred = toggleStarUpdate.newStarState)

                                    }
                                    is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                                        listItems[listItemIndex] =
                                            listItem.copy(starred = toggleStarUpdate.newStarState)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun failed(error: Error) {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(
                BusStopArrivalsScreenState.Failed(
                    busStop?.run {
                        BusStopArrivalListItemData.BusStopHeader(
                                busStopCode = code,
                                busStopDescription = description,
                                busStopRoadName = roadName,
                        )
                    }
                )
            )
        }
    }

    override fun onCleared() {
        stopBusArrivalFlowUseCase()
    }

    private suspend fun handleBusArrivalList(busArrivals: List<BusArrival>) {
        withContext(dispatcherProvider.computation) {
            if (!busArrivalListLock.tryLock()) return@withContext

            val busStop = this@BusStopArrivalsViewModel.busStop ?: return@withContext

            busArrivals.forEach { busArrival ->
                val arrivals = busArrival.arrivals

                val listItemIndex =
                    listItems.indexOfFirst {
                        it is BusStopArrivalListItemData.BusStopArrival
                                && it.busServiceNumber == busArrival.serviceNumber
                    }

                if (listItemIndex == -1) {
                    when (arrivals) {
                        is Arrivals.Arriving -> {
                            listItems.add(
                                BusStopArrivalListItemData.BusStopArrival.Arriving(
                                    busServiceNumber = busArrival.serviceNumber,
                                    destinationBusStopDescription =
                                    arrivals.nextArrivingBus.destination.busStopDescription,
                                    arrival = arrivals.nextArrivingBus.arrival,
                                    busType = arrivals.nextArrivingBus.type,
                                    wheelchairAccess = arrivals.nextArrivingBus.feature == "WAB",
                                    busLoad = arrivals.nextArrivingBus.load,
                                    busStop = busStop,
                                    starred = busArrival.starred
                                )
                            )
                        }
                        else -> {
                            listItems.add(
                                BusStopArrivalListItemData.BusStopArrival.NotArriving(
                                    busServiceNumber = busArrival.serviceNumber,
                                    reason = if (arrivals is Arrivals.NotOperating) {
                                        "Not Operating"
                                    } else {
                                        "No Data"
                                    },
                                    busStop = busStop,
                                    starred = busArrival.starred
                                )
                            )
                        }
                    }
                    return@forEach
                }

                when (val listItem = listItems[listItemIndex]) {
                    is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                        when (arrivals) {
                            is Arrivals.Arriving -> {
                                listItems[listItemIndex] = listItem.copy(
                                    arrival = arrivals.nextArrivingBus.arrival,
                                    destinationBusStopDescription =
                                    arrivals.nextArrivingBus.destination.busStopDescription,
                                    busType = arrivals.nextArrivingBus.type,
                                    wheelchairAccess = arrivals.nextArrivingBus.feature == "WAB",
                                    busLoad = arrivals.nextArrivingBus.load,
                                    busStop = busStop,
                                    starred = busArrival.starred,
                                )
                            }
                            else -> {
                                listItems.removeAt(listItemIndex)
                                listItems.add(
                                    BusStopArrivalListItemData.BusStopArrival.NotArriving(
                                        busServiceNumber = listItem.busServiceNumber,
                                        reason = if (arrivals is Arrivals.NotOperating) {
                                            "Not Operating"
                                        } else {
                                            "No Data"
                                        },
                                        busStop = busStop,
                                        starred = busArrival.starred
                                    )
                                )
                            }
                        }
                    }
                    is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                        when (arrivals) {
                            is Arrivals.Arriving -> {
                                listItems.removeAt(listItemIndex)
                                var lastArrivingItemIndex = listItems.indexOfLast {
                                    it is BusStopArrivalListItemData.BusStopArrival.Arriving
                                }
                                if (lastArrivingItemIndex == -1) {
                                    lastArrivingItemIndex = 3
                                }
                                listItems.add(
                                    lastArrivingItemIndex,
                                    BusStopArrivalListItemData.BusStopArrival.Arriving(
                                        busServiceNumber = listItem.busServiceNumber,
                                        destinationBusStopDescription =
                                        arrivals.nextArrivingBus.destination.busStopDescription,
                                        arrival = arrivals.nextArrivingBus.arrival,
                                        busType = arrivals.nextArrivingBus.type,
                                        wheelchairAccess =
                                        arrivals.nextArrivingBus.feature == "WAB",
                                        busLoad = arrivals.nextArrivingBus.load,
                                        busStop = busStop,
                                        starred = busArrival.starred
                                    )
                                )
                            }
                            else -> {
                                listItems[listItemIndex] = listItem.copy(
                                    reason = if (arrivals is Arrivals.NotOperating) {
                                        "Not Operating"
                                    } else {
                                        "No Data"
                                    },
                                    starred = busArrival.starred,
                                )
                            }
                        }
                    }
                }
            }

            busArrivalListLock.unlock()
        }
    }

    fun onStarToggle(newToggleState: Boolean, busServiceNumber: String) {
        viewModelScope.launch(coroutineContext) {
            if (!busArrivalListLock.tryLock()) return@launch

            val listItemIndex =
                listItems.indexOfFirst {
                    it is BusStopArrivalListItemData.BusStopArrival
                            && it.busServiceNumber == busServiceNumber
                }

            if (listItemIndex != -1) {
                when (val listItem = listItems[listItemIndex]) {
                    is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                        listItems[listItemIndex] = listItem.copy(starred = newToggleState)

                    }
                    is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                        listItems[listItemIndex] = listItem.copy(starred = newToggleState)
                    }
                }

                val busStopCode = busStop?.code

                if (busStopCode != null) {
                    toggleStar(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber,
                        toggleTo = newToggleState
                    )
                }
            }

            busArrivalListLock.unlock()
        }
    }

    fun onDispose() {
        pushMapEventUseCase(
            MapEvent.DeleteMarker(
                markerId = busStop?.code ?: return,
            )
        )
        job?.cancel()
        job = null
        bottomSheetInit = false
    }

    private var job: Job? = null

    private fun startListeningArrivals() {
        viewModelScope.launch(coroutineContext) {
            val busStop = this@BusStopArrivalsViewModel.busStop ?: return@launch

            job = viewModelScope.launch(coroutineContext) {
                getBusArrivalFlowUseCase(busStop.code)
                    .collect { busArrivalList ->
                        handleBusArrivalList(
                            busArrivalList
                        )
                    }
            }
        }
    }

    companion object {
        private const val TAG = "BusStopArrvlsVmDelegate"
    }
}