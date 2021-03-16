package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.model.busroute.BusRouteNavigationParams
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalFlowUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.StopBusArrivalFlowUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "BusStopArrivalsVM"

class BusStopArrivalsViewModel @Inject constructor(
    private val getBusArrivalFlowUseCase: GetBusArrivalFlowUseCase,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
    private val stopBusArrivalFlowUseCase: StopBusArrivalFlowUseCase,
    @Named("navigateToBusRoute")
    private val navigateToBusRoute: MutableSharedFlow<BusRouteNavigationParams>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit = { _, _ ->

    }

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

    private lateinit var busStop: BusStop

    internal val listItems = SnapshotStateList<BusStopArrivalListItemData>()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(Error())
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val busArrivalListLock = Mutex()

    fun init(busStop: BusStop) {
        this.busStop = busStop
        viewModelScope.launch(coroutineContext) {
            busArrivalListLock.withLock {
                if (listItems.isNotEmpty()) {
                    return@launch
                }
                pushInitListItems(busStop)
            }
            getBusArrivalFlowUseCase(busStop.code)
                .collect { busArrivalList ->
                    handleBusArrivalList(
                        busArrivalList
                    )
                }
        }
    }

    @WorkerThread
    private fun pushInitListItems(busStop: BusStop) {
//        listItems.add(BusStopArrivalListItemData.Header("Bus Stop"))

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
                    arrival = "Fetching..."
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
                                    busLoad = arrivals.nextArrivingBus.load
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
                                        }
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
                                        busLoad = arrivals.nextArrivingBus.load
                                    )
                                )
                            }
                            else -> {
                                listItems[listItemIndex] = listItem.copy(
                                    reason = if (arrivals is Arrivals.NotOperating) {
                                        "Not Operating"
                                    } else {
                                        "No Data"
                                    }
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

    companion object {
        private const val TAG = "BusStopArrvlsVmDelegate"
    }
}