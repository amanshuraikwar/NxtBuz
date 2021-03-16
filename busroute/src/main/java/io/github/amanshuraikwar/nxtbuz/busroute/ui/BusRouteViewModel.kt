package io.github.amanshuraikwar.nxtbuz.busroute.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRoutePreviousAllItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusRoute
import io.github.amanshuraikwar.nxtbuz.common.model.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.listitem.BusRouteItem
import io.github.amanshuraikwar.nxtbuz.listitem.BusRoutePreviousItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
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

    val listItems = SnapshotStateList<BusRouteListItemData>()

    lateinit var busStop: BusStop
    lateinit var busRoute: BusRoute

    fun init(busServiceNumber: String, busStop: BusStop?) {
        this.busStop = busStop ?: return
        viewModelScope.launch(coroutineContext) {
            busRoute = getBusRouteUseCase(
                busServiceNumber = busServiceNumber,
                busStopCode = busStop.code
            )
            pushInitListItems(busServiceNumber)
        }
    }

    private fun pushInitListItems(busServiceNumber: String) {
        val currentBusRouteNodeIndex = busRoute.busRouteNodeList.indexOfFirst {
            it.busStopCode == busStop.code
        }

        if (currentBusRouteNodeIndex == -1) {
            throw Exception(
                "Current bus stop code " +
                        "${busStop.code} for service " +
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

        //val listItems = mutableListOf<RecyclerViewListItem>()

//        listItems.add(
//            HeaderItem("Bus Service")
//        )

//        listItems.add(
//            BusRouteHeaderItem(
//                busStopCode = busStop?.code,
//                busServiceNumber = busServiceNumber,
//                totalBusStops = totalStops,
//                totalDistance = totalDistance,
//                originBusStopDescription = busRoute.originBusStopDescription,
//                destinationBusStopDescription = busRoute.destinationBusStopDescription,
//                starred = busRoute.starred,
//                onStarToggle = onStarToggle
//            )
//        )

//        listItems.add(BusRouteListItemData.Header("Bus Service"))
        listItems.add(
            BusRouteListItemData.BusRouteHeader(
                busServiceNumber = busServiceNumber,
                destinationBusStopDescription = busRoute.destinationBusStopDescription,
                originBusStopDescription = busRoute.originBusStopDescription,
            )
        )
        listItems.add(BusRouteListItemData.Header("Stops"))

        val currentSequenceNumber = busRoute.busRouteNodeList
            .find {
                it.busStopCode == busStop.code
            }
            ?.stopSequence
            ?: throw Exception(
                "Current bus stop ${busStop.code} is not in the bus route."
            )

//        listItems.add(
//            if (currentSequenceNumber > 1) {
//                HeaderItem("Bus Stops") {
//                    // TODO-amanshuraikwar (16 Feb 2021 11:45:25 PM): hide previous bus stops
//                }
//            } else {
//                HeaderItem("Bus Stops")
//            }
//        )

        if (currentSequenceNumber > 1) {
            listItems.add(
                BusRouteListItemData.BusRoutePreviousAll(
                    title = "See previous ${currentSequenceNumber - 1} bus stops"
                )
            )
        }

        busRoute.busRouteNodeList.forEachIndexed { _, busRouteNode: BusRouteNode ->
            listItems.add(
                when {
                    busRouteNode.stopSequence == currentSequenceNumber -> {
                        BusRouteListItemData.BusRouteNode.Current(
                            //busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                1 -> BusRouteListItemData.BusRouteNode.Position.ORIGIN
                                lastBusStopSequence ->
                                    BusRouteListItemData.BusRouteNode.Position.DESTINATION
                                else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                            },
//                            listOf("Fetching arrivals..."),
//                            TimeUtil.currentTimeStr(),
//                            {
//                                // TODO-amanshuraikwar (16 Feb 2021 11:48:40 PM): go to bus stop
//                            }
                        )
                    }
                    busRouteNode.stopSequence > currentSequenceNumber -> {
                        BusRouteListItemData.BusRouteNode.Next(
                            //busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                1 -> BusRouteListItemData.BusRouteNode.Position.ORIGIN
                                lastBusStopSequence ->
                                    BusRouteListItemData.BusRouteNode.Position.DESTINATION
                                else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                            },
//                            listOf("Fetching arrivals..."),
//                            TimeUtil.currentTimeStr(),
//                            {
//                                // TODO-amanshuraikwar (16 Feb 2021 11:48:40 PM): go to bus stop
//                            }
                        )
                    }
                    else -> return@forEachIndexed
                }
            )
        }

//        _screenState.emit(BusRouteScreenState.Success(listItems))
    }

    fun previousAllClicked() {
        viewModelScope.launch(coroutineContext) {
            // TODO: 16/03/21 lock
            val previousBusRouteNodeList = busRoute
                .busRouteNodeList.sortedBy { it.stopSequence }
                .subList(
                    0,
                    busRoute.busRouteNodeList.indexOfFirst { it.busStopCode == busStop.code }
                )

            if (previousBusRouteNodeList.isEmpty()) return@launch

            val lastBusStopSequence = busRoute.busRouteNodeList
                .maxBy { it.stopSequence }
                ?.stopSequence
                ?: throw Exception(
                    "Could not find max bus stop sequence for route $busRoute."
                )

            val previousBusStopItemList: List<BusRouteListItemData.BusRouteNode.Previous> =
                previousBusRouteNodeList.map { busRouteNode ->
                    Log.i(TAG, "previousAllClicked: ${busRouteNode.stopSequence}")
                    BusRouteListItemData.BusRouteNode.Previous(
//                        busRouteNode.busStopCode,
                        busRouteNode.busStopDescription,
                        when (busRouteNode.stopSequence) {
                            previousBusRouteNodeList[0].stopSequence ->
                                BusRouteListItemData.BusRouteNode.Position.ORIGIN
                            lastBusStopSequence ->
                                BusRouteListItemData.BusRouteNode.Position.DESTINATION
                            else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                        },
//                        onGoToBusStopClick = ::goToBusStop,
//                        onClick = ::startSecondaryArrivals,
                    )
                }

            val previousAllIndex =
                listItems.indexOfFirst { it is BusRouteListItemData.BusRoutePreviousAll }

            if (previousAllIndex != -1) {
                listItems.removeAt(previousAllIndex)
                listItems.addAll(previousAllIndex, previousBusStopItemList)
            }
        }
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