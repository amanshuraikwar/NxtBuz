package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    @Named("navigateToBusRoute")
    private val navigateToBusRoute: MutableSharedFlow<BusRouteNavigationParams>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    val onBusServiceClicked: (busServiceNumber: String) -> Unit = {
        viewModelScope.launch(coroutineContext) {
            navigateToBusRoute.emit(
                BusRouteNavigationParams(
                    busServiceNumber = it,
                    busStop = busStop
                )
            )
        }
    }

    private var busStop: BusStop? = null

    internal var listItems = SnapshotStateList<BusStopArrivalListItemData>()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(Error())
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val busArrivalListLock = Mutex()

    fun init(busStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            val busStop = getBusStopUseCase(busStopCode)

            busArrivalListLock.withLock {
                addBusStopMapMarker(busStop = busStop)
                if (this@BusStopArrivalsViewModel.busStop == busStop) {
                    return@launch
                }
                this@BusStopArrivalsViewModel.busStop = busStop
                pushInitListItems(busStop)
            }

            listenToggleStarUpdate()

            getBusArrivalFlowUseCase(busStop.code)
                .collect { busArrivalList ->
                    handleBusArrivalList(
                        busArrivalList
                    )
                }
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

    @WorkerThread
    private fun pushInitListItems(busStop: BusStop) {
        stopBusArrivalFlowUseCase()

        listItems = SnapshotStateList()

        listItems.add(
            BusStopArrivalListItemData.BusStopHeader(
                busStopCode = busStop.code,
                busStopDescription = busStop.description,
                busStopRoadName = busStop.roadName,
            )
        )

        listItems.add(BusStopArrivalListItemData.Header("Departures"))

        listItems.addAll(
            busStop.operatingBusList.map { bus ->
                BusStopArrivalListItemData.BusStopArrival.Arriving(
                    busServiceNumber = bus.serviceNumber,
                    destinationBusStopDescription = "Fetching...",
                    busLoad = BusLoad.SEA,
                    wheelchairAccess = false,
                    busType = BusType.SD,
                    arrival = "Fetching...",
                    busStop = busStop,
                    starred = false,
                )
            }
        )
    }

    private fun failed(error: Error) {
        viewModelScope.launch(coroutineContext) {
        }
    }

    fun updateBottomSheetSlideOffset(slideOffset: Float) {
        viewModelScope.launch(coroutineContext) {
            bottomSheetSlideOffsetFlow.value = slideOffset
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
                if (listItemIndex == -1) return@forEach

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
                                        wheelchairAccess = arrivals.nextArrivingBus.feature == "WAB",
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

    private fun onMapMarkerClicked(markerId: String) {
        viewModelScope.launch(errorHandler) {
            onBusServiceClicked(markerId)
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
    }

    companion object {
        private const val TAG = "BusStopArrvlsVmDelegate"
    }
}