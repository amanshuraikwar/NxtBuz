package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalsScreenState
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.BusStopArrivalsLoop
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.IsBusServiceStarredUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusServiceStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleStarUpdateUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusStopArrivalsViewModel @Inject constructor(
    private val getBusStopUseCase: GetBusStopUseCase,
    private val isStarredUseCase: IsBusServiceStarredUseCase,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val toggleStar: ToggleBusServiceStarUseCase,
    private val toggleStarUpdateUseCase: ToggleStarUpdateUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val toggleBusStopStarUseCase: ToggleBusStopStarUseCase,
    private val navigationUtil: NavigationUtil,
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
        failed()
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation
    private val busArrivalListLock = Mutex()
    internal var bottomSheetInit = false
    private var loop: BusStopArrivalsLoop? = null
    private var listenStarUpdatesJob: Job? = null
    private var listenBusStopStarUpdatesJob: Job? = null

    fun init(busStopCode: String) {
        FirebaseCrashlytics.getInstance().setCustomKey(
            "viewModel", "$TAG-$busStopCode"
        )

        viewModelScope.launch(coroutineContext) {
            var busStop = this@BusStopArrivalsViewModel.busStop

            if (busStopCode == busStop?.code) {
                busArrivalListLock.withLock {
                    listItems = SnapshotStateList()
                }

                busStop = getBusStopUseCase(busStopCode) ?: return@launch

                _screenState.emit(
                    BusStopArrivalsScreenState.Success(
                        BusStopArrivalListItemData.BusStopHeader(
                            id = "bus-stop-arrivals-screen-${busStop.code}-header",
                            busStopCode = busStop.code,
                            busStopDescription = busStop.description,
                            busStopRoadName = busStop.roadName,
                            starred = busStop.isStarred
                        ),
                        listItems
                    )
                )
            } else {
                _screenState.emit(BusStopArrivalsScreenState.Fetching)

                busStop = getBusStopUseCase(busStopCode) ?: return@launch

                busArrivalListLock.withLock {
                    listItems = SnapshotStateList()
                }

                _screenState.emit(
                    BusStopArrivalsScreenState.Success(
                        BusStopArrivalListItemData.BusStopHeader(
                            id = "bus-stop-arrivals-screen-${busStop.code}-header",
                            busStopCode = busStop.code,
                            busStopDescription = busStop.description,
                            busStopRoadName = busStop.roadName,
                            starred = busStop.isStarred
                        ),
                        listItems
                    )
                )
            }

            busArrivalListLock.withLock {
                this@BusStopArrivalsViewModel.busStop = busStop
            }

            listenToggleStarUpdate()
            listenToggleBusStopStarUpdate()
            waitForBottomSheetInit()
            startListeningArrivals()

            addBusStopMapMarker(busStop = busStop)
        }
    }

    private suspend fun waitForBottomSheetInit() {
        while (true) {
            if (bottomSheetInit) break
            delay(300)
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

        pushMapEventUseCase(
            MapEvent.MoveCenter(
                lat = busStop.latitude,
                lng = busStop.longitude
            )
        )
    }

    private fun listenToggleStarUpdate() {
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        listenStarUpdatesJob = viewModelScope.launch(coroutineContext) {
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
                                    else -> {
                                        // do nothing
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun failed() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(
                BusStopArrivalsScreenState.Failed(
                    busStop?.run {
                        BusStopArrivalListItemData.BusStopHeader(
                            busStopCode = code,
                            busStopDescription = description,
                            busStopRoadName = roadName,
                            id = "bus-stop-arrivals-screen-$code-header",
                            starred = isStarred
                        )
                    }
                )
            )
        }
    }

    private suspend fun handleBusArrivalList(busStopArrivals: List<BusStopArrival>) {
        withContext(dispatcherProvider.computation) {
            if (busArrivalListLock.isLocked) return@withContext

            busArrivalListLock.withLock {
                val busStop = this@BusStopArrivalsViewModel.busStop ?: return@withContext

                busStopArrivals.forEach { busArrival ->
                    val arrivals = busArrival.busArrivals

                    val listItemIndex =
                        listItems.indexOfFirst {
                            it is BusStopArrivalListItemData.BusStopArrival
                                    && it.busServiceNumber == busArrival.busServiceNumber
                        }

                    if (listItemIndex == -1) {
                        when (arrivals) {
                            is BusArrivals.Arriving -> {
                                listItems.add(
                                    BusStopArrivalListItemData.BusStopArrival.Arriving(
                                        busServiceNumber = busArrival.busServiceNumber,
                                        destinationBusStopDescription =
                                        arrivals.nextArrivingBus.destination.busStopDescription,
                                        arrivingBusList = mutableListOf(arrivals.nextArrivingBus)
                                            .apply {
                                                addAll(arrivals.followingArrivingBusList)
                                            },
                                        busStop = busStop,
                                        starred = isStarredUseCase(
                                            busServiceNumber = busArrival.busServiceNumber,
                                            busStopCode = busArrival.busStopCode,
                                        )
                                    )
                                )
                            }
                            else -> {
                                listItems.add(
                                    BusStopArrivalListItemData.BusStopArrival.NotArriving(
                                        busServiceNumber = busArrival.busServiceNumber,
                                        reason = if (arrivals is BusArrivals.NotOperating) {
                                            "Not Operating"
                                        } else {
                                            "No Data"
                                        },
                                        busStop = busStop,
                                        starred = isStarredUseCase(
                                            busServiceNumber = busArrival.busServiceNumber,
                                            busStopCode = busArrival.busStopCode,
                                        )
                                    )
                                )
                            }
                        }
                        return@forEach
                    }

                    when (val listItem = listItems[listItemIndex]) {
                        is BusStopArrivalListItemData.BusStopArrival.Arriving -> {
                            when (arrivals) {
                                is BusArrivals.Arriving -> {
                                    listItems[listItemIndex] = listItem.copy(
                                        destinationBusStopDescription =
                                        arrivals.nextArrivingBus.destination.busStopDescription,
                                        busStop = busStop,
                                        arrivingBusList = mutableListOf(arrivals.nextArrivingBus)
                                            .apply {
                                                addAll(arrivals.followingArrivingBusList)
                                            },
                                        starred = isStarredUseCase(
                                            busServiceNumber = busArrival.busServiceNumber,
                                            busStopCode = busArrival.busStopCode,
                                        )
                                    )
                                }
                                else -> {
                                    listItems.removeAt(listItemIndex)
                                    listItems.add(
                                        BusStopArrivalListItemData.BusStopArrival.NotArriving(
                                            busServiceNumber = listItem.busServiceNumber,
                                            reason = if (arrivals is BusArrivals.NotOperating) {
                                                "Not Operating"
                                            } else {
                                                "No Data"
                                            },
                                            busStop = busStop,
                                            starred = isStarredUseCase(
                                                busServiceNumber = busArrival.busServiceNumber,
                                                busStopCode = busArrival.busStopCode,
                                            )
                                        )
                                    )
                                }
                            }
                        }
                        is BusStopArrivalListItemData.BusStopArrival.NotArriving -> {
                            when (arrivals) {
                                is BusArrivals.Arriving -> {
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
                                            arrivingBusList = mutableListOf(arrivals.nextArrivingBus)
                                                .apply {
                                                    addAll(arrivals.followingArrivingBusList)
                                                },
                                            busStop = busStop,
                                            starred = isStarredUseCase(
                                                busServiceNumber = busArrival.busServiceNumber,
                                                busStopCode = busArrival.busStopCode,
                                            )
                                        )
                                    )
                                }
                                else -> {
                                    listItems[listItemIndex] = listItem.copy(
                                        reason = if (arrivals is BusArrivals.NotOperating) {
                                            "Not Operating"
                                        } else {
                                            "No Data"
                                        },
                                        starred = isStarredUseCase(
                                            busServiceNumber = busArrival.busServiceNumber,
                                            busStopCode = busArrival.busStopCode,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun onStarToggle(newToggleState: Boolean, busServiceNumber: String) {
        viewModelScope.launch(coroutineContext) {
            if (busArrivalListLock.isLocked) return@launch

            busArrivalListLock.withLock {
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
            }
        }
    }

    fun onDispose() {
        pushMapEventUseCase(
            MapEvent.DeleteMarker(
                markerId = busStop?.code ?: return,
            )
        )
        loop?.stop()
        loop = null
        bottomSheetInit = false
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        listenBusStopStarUpdatesJob?.cancel()
        listenBusStopStarUpdatesJob = null
        _screenState.value = BusStopArrivalsScreenState.Fetching
    }

    private fun startListeningArrivals() {
        loop?.stop()
        loop = null
        loop = BusStopArrivalsLoop(
            busStopCode = busStop?.code ?: return,
            getBusArrivalsUseCase = getBusArrivalsUseCase,
            dispatcher = dispatcherProvider.arrivalService,
            coroutineScope = viewModelScope
        )
        loop?.startAndCollect(coroutineContext = coroutineContext) { busArrivalList ->
            handleBusArrivalList(
                busArrivalList
            )
        }
    }

    fun onGoToBusStopClicked() {
        busStop?.let { busStop ->
            navigationUtil.goTo(busStop.latitude, busStop.longitude)
        }
    }

    fun onBusStopStarToggle(busStopCode: String, newStarState: Boolean) {
        viewModelScope.launch(coroutineContext) {
            toggleBusStopStarUseCase(busStopCode = busStopCode, toggleTo = newStarState)
        }
    }

    private fun listenToggleBusStopStarUpdate() {
        listenBusStopStarUpdatesJob?.cancel()
        listenBusStopStarUpdatesJob = null
        listenBusStopStarUpdatesJob = viewModelScope.launch(coroutineContext) {
            toggleBusStopStarUseCase.updates()
                .collect { busStop ->
                    busArrivalListLock.withLock {
                        (_screenState.value as? BusStopArrivalsScreenState.Success)
                            ?.header
                            ?.takeIf {
                                it.busStopCode == busStop.code
                            }
                            ?.updateStarred(busStop.isStarred)
                    }
                }
        }
    }

    companion object {
        private const val TAG = "BusStopArrvlsVmDelegate"
    }
}