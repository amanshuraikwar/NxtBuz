package io.github.amanshuraikwar.nxtbuz.busroute.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "BusRouteViewModel"

class BusRouteViewModel @Inject constructor(
    private val getBusRouteUseCase: GetBusRouteUseCase,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit = { _, _ ->

    }

    private val _screenState = MutableSharedFlow<BusRouteScreenState>(replay = 1)
    val screenState: SharedFlow<BusRouteScreenState> = _screenState

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(Error())
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    fun init(busServiceNumber: String, busStop: BusStop?) {
        viewModelScope.launch(coroutineContext) {
            pushInitListItems(busServiceNumber, busStop)
        }
    }

    private suspend fun pushInitListItems(busServiceNumber: String, busStop: BusStop?) {
        val busRoute = getBusRouteUseCase(
            busServiceNumber = busServiceNumber,
            busStopCode = busStop?.code
        )

        val currentBusRouteNodeIndex = if (busStop != null) {
            busRoute.busRouteNodeList.indexOfFirst {
                it.busStopCode == busStop.code
            }
        } else {
            -1
        }

        if (currentBusRouteNodeIndex == -1) {
            throw Exception(
                "Current bus stop code " +
                        "${busStop?.code} for service " +
                        "$busServiceNumber is -1."
            )
        }

        val lastBusStopSequence = busRoute.busRouteNodeList
            .maxBy { it.stopSequence }
            ?.stopSequence
            ?: throw Exception("Could not find max bus stop sequence for route $busRoute.")

        val totalDistance =
            busRoute
                .busRouteNodeList
                .find { it.stopSequence == lastBusStopSequence }
                ?.distance
                ?: throw Exception(
                    "No bus route node found for stop sequence $lastBusStopSequence."
                )

        val totalStops = busRoute.busRouteNodeList.size

        val listItems = mutableListOf<RecyclerViewListItem>()

        listItems.add(
            HeaderItem("Bus Service")
        )

        listItems.add(
            BusRouteHeaderItem(
                busStopCode = busStop?.code,
                busServiceNumber = busServiceNumber,
                totalBusStops = totalStops,
                totalDistance = totalDistance,
                originBusStopDescription = busRoute.originBusStopDescription,
                destinationBusStopDescription = busRoute.destinationBusStopDescription,
                starred = busRoute.starred,
                onStarToggle = onStarToggle
            )
        )

        val currentSequenceNumber = if (busStop != null) {
            busRoute.busRouteNodeList
                .find {
                    it.busStopCode == busStop.code
                }
                ?.stopSequence
                ?: throw Exception(
                    "Current bus stop ${busStop.code} is not in the bus route."
                )
        } else {
            0
        }

        listItems.add(
            if (currentSequenceNumber > 1) {
                HeaderItem("Bus Stops") {
                    // TODO-amanshuraikwar (16 Feb 2021 11:45:25 PM): hide previous bus stops
                }
            } else {
                HeaderItem("Bus Stops")
            }
        )


        if (currentSequenceNumber > 1) {
            listItems.add(
                BusRoutePreviousAllItem(
                    "",
                    "See previous ${currentSequenceNumber - 1} bus stops",
                    BusRouteItem.Position.MIDDLE,
                    {
                        // TODO-amanshuraikwar (16 Feb 2021 11:48:02 PM): previous all clicked
                    }
                )
            )
        }

        busRoute.busRouteNodeList.forEachIndexed { _, busRouteNode: BusRouteNode ->
            listItems.add(
                when {
                    busRouteNode.stopSequence == currentSequenceNumber -> {
                        BusRouteCurrentItem(
                            busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                1 -> BusRouteItem.Position.ORIGIN
                                lastBusStopSequence -> BusRouteItem.Position.DESTINATION
                                else -> BusRouteItem.Position.MIDDLE
                            },
                            listOf("Fetching arrivals..."),
                            TimeUtil.currentTimeStr(),
                            {
                                // TODO-amanshuraikwar (16 Feb 2021 11:48:40 PM): go to bus stop
                            }
                        )
                    }
                    busRouteNode.stopSequence > currentSequenceNumber -> {
                        BusRouteNextItem(
                            busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                1 -> BusRouteItem.Position.ORIGIN
                                lastBusStopSequence -> BusRouteItem.Position.DESTINATION
                                else -> BusRouteItem.Position.MIDDLE
                            },
                            onGoToBusStopClick = {
                                // TODO-amanshuraikwar (16 Feb 2021 11:48:52 PM):
                            },
                            onClick = {
                                // TODO-amanshuraikwar (16 Feb 2021 11:49:07 PM): start secondary arrivals
                            }
                        )
                    }
                    else -> return@forEachIndexed
                }
            )
        }

        _screenState.emit(BusRouteScreenState.Success(listItems))
    }

    private fun failed(error: Error) {
        viewModelScope.launch {
            _screenState.emit(BusRouteScreenState.Failed(error))
        }
    }

    fun updateBottomSheetSlideOffset(slideOffset: Float) {
        viewModelScope.launch(coroutineContext) {
            bottomSheetSlideOffsetFlow.value = slideOffset
        }
    }
}