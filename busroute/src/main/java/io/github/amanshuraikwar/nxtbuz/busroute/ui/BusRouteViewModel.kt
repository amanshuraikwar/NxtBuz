package io.github.amanshuraikwar.nxtbuz.busroute.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busroute.loop.ArrivalsLoop
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "BusRouteViewModel"

class BusRouteViewModel @Inject constructor(
    private val getBusRouteUseCase: GetBusRouteUseCase,
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit = { _, _ ->

    }

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed(Error())
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation

    val listItems = SnapshotStateList<BusRouteListItemData>()

    lateinit var busStop: BusStop
    lateinit var busRoute: BusRoute
    lateinit var busServiceNumber: String

    private var primaryArrivalsLoop: ArrivalsLoop? = null
    private var secondaryArrivalsLoop: ArrivalsLoop? = null
    private val listItemsLock = Mutex()

    fun init(busServiceNumber: String, busStop: BusStop?) {
        this.busServiceNumber = busServiceNumber
        this.busStop = busStop ?: return
        viewModelScope.launch(coroutineContext) {
            busRoute = getBusRouteUseCase(
                busServiceNumber = busServiceNumber,
                busStopCode = busStop.code
            )
            listItemsLock.withLock {
                if (listItems.isNotEmpty()) {
                    return@launch
                }
                pushInitListItems(busServiceNumber)
            }
            startPrimaryBusArrivalsLoop(busServiceNumber)
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
                            busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                1 -> BusRouteListItemData.BusRouteNode.Position.ORIGIN
                                lastBusStopSequence ->
                                    BusRouteListItemData.BusRouteNode.Position.DESTINATION
                                else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                            },
                        )
                    }
                    busRouteNode.stopSequence > currentSequenceNumber -> {
                        BusRouteListItemData.BusRouteNode.Next(
                            busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                1 -> BusRouteListItemData.BusRouteNode.Position.ORIGIN
                                lastBusStopSequence ->
                                    BusRouteListItemData.BusRouteNode.Position.DESTINATION
                                else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                            },
                        )
                    }
                    else -> return@forEachIndexed
                }
            )
        }
    }

    fun previousAllClicked() {
        viewModelScope.launch(coroutineContext) {
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
                        busRouteNode.busStopCode,
                        busRouteNode.busStopDescription,
                        when (busRouteNode.stopSequence) {
                            previousBusRouteNodeList[0].stopSequence ->
                                BusRouteListItemData.BusRouteNode.Position.ORIGIN
                            lastBusStopSequence ->
                                BusRouteListItemData.BusRouteNode.Position.DESTINATION
                            else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                        },
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

    private suspend fun startPrimaryBusArrivalsLoop(
        busServiceNumber: String,
    ) {

        primaryArrivalsLoop?.stop()

        val arrivalsLoop =
            ArrivalsLoop(
                busServiceNumber = busServiceNumber,
                busStopCode = busStop.code,
                getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
                dispatcher = dispatcherProvider.pool8
            )

        primaryArrivalsLoop = arrivalsLoop

        arrivalsLoop
            .start(viewModelScope)
            .catch { throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }
            .filterNotNull()
            .collect { arrivalsLoopData ->
                // check for busStopCode & busServiceNumber
                // to prevent pushing any dangling loop output to UI
                if (arrivalsLoopData.busStopCode == busStop.code
                    && arrivalsLoopData.busServiceNumber == busServiceNumber
                ) {
                    handlePrimaryArrivals(arrivalsLoopData.busArrival)
                } else {
                    arrivalsLoop.stop()
                }
            }
    }

    private suspend fun handlePrimaryArrivals(busArrival: BusArrival) {

        if (!listItemsLock.tryLock()) return

        val listItemIndex = listItems.indexOfFirst {
            it is BusRouteListItemData.BusRouteNode.Current
                    // TODO: 16/03/21 update for bus stop code equality
                    && it.busStopDescription == busStop.description
        }

        if (listItemIndex == -1) return
        val currentListItemData =
            listItems[listItemIndex] as? BusRouteListItemData.BusRouteNode.Current ?: return

        listItems[listItemIndex] = currentListItemData.copy(
            arrivalState = BusRouteListItemData.ArrivalState.Active(
                arrivals = busArrival.arrivals,
                lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}"
            )
        )

        listItemsLock.unlock()
    }

    private fun failed(error: Error) {
        viewModelScope.launch {
        }
    }

    fun updateBottomSheetSlideOffset(slideOffset: Float) {
        viewModelScope.launch(coroutineContext) {
            bottomSheetSlideOffsetFlow.value = slideOffset
        }
    }

    fun onExpand(busStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            secondaryArrivalsLoop?.stop()

            secondaryArrivalsLoop?.busStopCode?.let { busStopCode ->
                val listItemIndex = listItems.indexOfFirst {
                    it is BusRouteListItemData.BusRouteNode.Next
                            && it.busStopCode == busStopCode
                }

                val listItemData =
                    listItems[listItemIndex] as? BusRouteListItemData.BusRouteNode.Next
                        ?: return@launch

                listItems[listItemIndex] = listItemData.copy(
                    arrivalState = BusRouteListItemData.ArrivalState.Inactive
                )
            }

            startSecondaryArrivals(busStopCode)
        }
    }

    fun onShrink(busStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            if (busStopCode == secondaryArrivalsLoop?.busStopCode) {
                secondaryArrivalsLoop?.stop()

                secondaryArrivalsLoop?.busStopCode?.let { busStopCode ->
                    val listItemIndex = listItems.indexOfFirst {
                        it is BusRouteListItemData.BusRouteNode.Next
                                && it.busStopCode == busStopCode
                    }

                    val listItemData =
                        listItems[listItemIndex] as? BusRouteListItemData.BusRouteNode.Next
                            ?: return@launch

                    listItems[listItemIndex] = listItemData.copy(
                        arrivalState = BusRouteListItemData.ArrivalState.Inactive
                    )
                }

                secondaryArrivalsLoop = null
            }
        }
    }

    private fun startSecondaryArrivals(busStopCode: String) {
        viewModelScope.launch(dispatcherProvider.computation) {

            listItemsLock.lock()

            val listItemIndex = listItems.indexOfFirst {
                it is BusRouteListItemData.BusRouteNode.Next
                        && it.busStopCode == busStopCode
            }

            if (listItemIndex == -1) return@launch
            val listItemData =
                listItems[listItemIndex] as? BusRouteListItemData.BusRouteNode.Next ?: return@launch

            listItems[listItemIndex] = listItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Fetching
            )

            val arrivalsLoop =
                ArrivalsLoop(
                    busServiceNumber = busServiceNumber,
                    busStopCode = busStopCode,
                    getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
                    dispatcher = dispatcherProvider.pool8
                )

            secondaryArrivalsLoop = arrivalsLoop

            listItemsLock.unlock()

            arrivalsLoop.start(viewModelScope)
                .catch { throwable ->
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }
                .filterNotNull()
                .collect { arrivalsLoopData ->
                    // check for busStopCode & busServiceNumber
                    // to prevent pushing any dangling loop output to UI
                    if (arrivalsLoopData.busStopCode == busStopCode
                        && arrivalsLoopData.busServiceNumber == busServiceNumber
                    ) {
                        handleSecondaryArrivals(busStopCode, arrivalsLoopData.busArrival)
                    } else {
                        arrivalsLoop.stop()
                    }
                }

        }
    }

    private suspend fun handleSecondaryArrivals(busStopCode: String, busArrival: BusArrival) {

        if (!listItemsLock.tryLock()) return

        //delay(3000)

        val listItemIndex = listItems.indexOfFirst {
            it is BusRouteListItemData.BusRouteNode.Next
                    && it.busStopCode == busStopCode
        }

        if (listItemIndex == -1) return
        val listItemData =
            listItems[listItemIndex] as? BusRouteListItemData.BusRouteNode.Next ?: return

        listItems[listItemIndex] = listItemData.copy(
            arrivalState = BusRouteListItemData.ArrivalState.Active(
                arrivals = busArrival.arrivals,
                lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}",
            )
        )

        listItemsLock.unlock()
    }
}