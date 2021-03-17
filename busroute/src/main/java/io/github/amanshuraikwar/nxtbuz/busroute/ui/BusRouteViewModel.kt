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

    lateinit var currentBusStop: BusStop
    lateinit var busRoute: BusRoute
    lateinit var busServiceNumber: String

    private var primaryArrivalsLoop: ArrivalsLoop? = null
    private var secondaryArrivalsLoop: ArrivalsLoop? = null
    private val listItemsLock = Mutex()

    fun init(busServiceNumber: String, busStop: BusStop?) {
        viewModelScope.launch(coroutineContext) {
            listItemsLock.withLock {
                if (listItems.isNotEmpty()) {
                    return@launch
                }

                this@BusRouteViewModel.busServiceNumber = busServiceNumber
                this@BusRouteViewModel.currentBusStop = busStop ?: return@launch

                busRoute = getBusRouteUseCase(
                    busServiceNumber = this@BusRouteViewModel.busServiceNumber,
                    busStopCode = this@BusRouteViewModel.currentBusStop.code
                )

                pushInitListItems()

                startPrimaryBusArrivalsLoop()
            }
        }
    }

    private fun pushInitListItems() {
        val currentBusRouteNodeIndex = busRoute.busRouteNodeList.indexOfFirst {
            it.busStopCode == currentBusStop.code
        }

        if (currentBusRouteNodeIndex == -1) {
            throw Exception(
                "Current bus stop code " +
                        "${currentBusStop.code} for service " +
                        "$busServiceNumber is -1."
            )
        }

        val lastBusStopSequence = busRoute.busRouteNodeList
            .maxByOrNull { it.stopSequence }
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

        val currentSequenceNumber: Int = busRoute.busRouteNodeList
            .findLast {
                it.busStopCode == currentBusStop.code
            }
            ?.stopSequence
            ?: throw Exception(
                "Current bus stop ${currentBusStop.code} is not in the bus route."
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
                            busRouteNode.stopSequence.toPosition(lastBusStopSequence),
                        )
                    }
                    busRouteNode.stopSequence > currentSequenceNumber -> {
                        BusRouteListItemData.BusRouteNode.Next(
                            busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            busRouteNode.stopSequence.toPosition(lastBusStopSequence)
                        )
                    }
                    else -> return@forEachIndexed
                }
            )
        }
    }

    private fun Int.toPosition(
        lastBusStopSequence: Int
    ): BusRouteListItemData.BusRouteNode.Position {
        return when (this) {
            1 -> BusRouteListItemData.BusRouteNode.Position.ORIGIN
            lastBusStopSequence ->
                BusRouteListItemData.BusRouteNode.Position.DESTINATION
            else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
        }
    }

    fun previousAllClicked() {
        viewModelScope.launch(coroutineContext) {
            val previousBusRouteNodeList = busRoute
                .busRouteNodeList.sortedBy { it.stopSequence }
                .subList(
                    0,
                    busRoute.busRouteNodeList.indexOfFirst { it.busStopCode == currentBusStop.code }
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

    private suspend fun startPrimaryBusArrivalsLoop() {
        viewModelScope.launch(coroutineContext) {
            primaryArrivalsLoop?.stop()

            val arrivalsLoop =
                ArrivalsLoop(
                    busServiceNumber = busServiceNumber,
                    busStopCode = currentBusStop.code,
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
                    if (arrivalsLoopData.busStopCode == currentBusStop.code
                        && arrivalsLoopData.busServiceNumber == busServiceNumber
                    ) {
                        if (!listItemsLock.tryLock()) return@collect
                        //handlePrimaryArrivals(arrivalsLoopData.busArrival, currentBusStop.code)
                        updateToActive<BusRouteListItemData.BusRouteNode.Current>(
                            arrivalsLoopData.busArrival,
                            currentBusStop.code
                        )
                        listItemsLock.unlock()
                    } else {
                        arrivalsLoop.stop()
                    }
                }
        }
    }

    private inline fun <reified T> List<Any>.find(predicate: (T) -> Boolean): Pair<Int, T>? {
        val listItemIndex = this.indexOfFirst {
            it is T && predicate(it)
        }

        if (listItemIndex == -1) return null
        val listItem = listItems[listItemIndex] as? T ?: return null

        return Pair(listItemIndex, listItem)
    }

    private fun handlePrimaryArrivals(
        busArrival: BusArrival,
        busStopCode: String,
    ) {
        updateToActive<BusRouteListItemData.BusRouteNode.Current>(busArrival, busStopCode)
//        val temp: Pair<Int, BusRouteListItemData.BusRouteNode.Current> =
//            listItems.find { it.busStopCode == busStopCode } ?: return
//
//        val (listItemIndex, currentListItemData) = temp
//
//        listItems[listItemIndex] = currentListItemData.copy(
//            arrivalState = BusRouteListItemData.ArrivalState.Active(
//                arrivals = busArrival.arrivals,
//                lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}"
//            )
//        )
    }

    private inline fun <reified T : BusRouteListItemData.BusRouteNode> updateToActive(
        busArrival: BusArrival,
        busStopCode: String,
    ): Boolean {
        val temp: Pair<Int, T> =
            listItems.find { it.busStopCode == busStopCode } ?: return false

        val (listItemIndex, currentListItemData) = temp

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Current) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Active(
                    arrivals = busArrival.arrivals,
                    lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}"
                )
            )
            return true
        }

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Next) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Active(
                    arrivals = busArrival.arrivals,
                    lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}"
                )
            )
            return true
        }

        return false
    }

    private inline fun <reified T : BusRouteListItemData.BusRouteNode> updateToFetching(
        busStopCode: String,
    ): Boolean {
        val temp: Pair<Int, T> =
            listItems.find { it.busStopCode == busStopCode } ?: return false

        val (listItemIndex, currentListItemData) = temp

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Current) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Fetching
            )
            return true
        }

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Next) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Fetching
            )
            return true
        }

        return false
    }

    private inline fun <reified T : BusRouteListItemData.BusRouteNode> updateToInactive(
        busStopCode: String,
    ): Boolean {
        val temp: Pair<Int, T> =
            listItems.find { it.busStopCode == busStopCode } ?: return false

        val (listItemIndex, currentListItemData) = temp

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Current) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Inactive
            )
            return true
        }

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Next) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Inactive
            )
            return true
        }

        return false
    }

    private fun failed(error: Error) {}

    fun onExpand(expandingBusStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            if (!(updateToFetching<BusRouteListItemData.BusRouteNode.Next>(
                    expandingBusStopCode)
                        || updateToFetching<BusRouteListItemData.BusRouteNode.Previous>(
                    expandingBusStopCode))
            ) {
                return@launch
            }

            listItemsLock.withLock {
                secondaryArrivalsLoop?.stop()

                secondaryArrivalsLoop?.busStopCode?.let { currentSecondaryBusStopCode ->
                    updateToInactive<BusRouteListItemData.BusRouteNode.Next>(
                        currentSecondaryBusStopCode)
                        || updateToInactive<BusRouteListItemData.BusRouteNode.Previous>(
                        currentSecondaryBusStopCode)
                }

                startSecondaryArrivals(expandingBusStopCode)
            }
        }
    }

    fun onCollapse(busStopCode: String) {
//        viewModelScope.launch(coroutineContext) {
//            if (busStopCode == secondaryArrivalsLoop?.busStopCode) {
//                secondaryArrivalsLoop?.stop()
//
//                secondaryArrivalsLoop?.busStopCode?.let { busStopCode ->
//                    val listItemIndex = listItems.indexOfFirst {
//                        it is BusRouteListItemData.BusRouteNode.Next
//                                && it.busStopCode == busStopCode
//                    }
//
//                    val listItemData =
//                        listItems[listItemIndex] as? BusRouteListItemData.BusRouteNode.Next
//                            ?: return@launch
//
//                    listItems[listItemIndex] = listItemData.copy(
//                        arrivalState = BusRouteListItemData.ArrivalState.Inactive
//                    )
//                }
//
//                secondaryArrivalsLoop = null
//            }
//        }
    }

    private fun startSecondaryArrivals(secondaryBusStopCode: String) {
        val arrivalsLoop =
            ArrivalsLoop(
                busServiceNumber = busServiceNumber,
                busStopCode = secondaryBusStopCode,
                getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
                dispatcher = dispatcherProvider.pool8
            )

        secondaryArrivalsLoop = arrivalsLoop

        arrivalsLoop.start(viewModelScope)
            .catch { throwable ->
                FirebaseCrashlytics.getInstance().recordException(throwable)
            }
            .filterNotNull()
            .onEach { arrivalsLoopData ->
                // check for busStopCode & busServiceNumber
                // to prevent pushing any dangling loop output to UI
                if (arrivalsLoopData.busStopCode == secondaryBusStopCode
                    && arrivalsLoopData.busServiceNumber == busServiceNumber
                ) {
                    if (!listItemsLock.tryLock()) return@onEach
                    updateToActive<BusRouteListItemData.BusRouteNode.Next>(
                        arrivalsLoopData.busArrival,
                        secondaryBusStopCode
                    )
                    updateToActive<BusRouteListItemData.BusRouteNode.Previous>(
                        arrivalsLoopData.busArrival,
                        secondaryBusStopCode
                    )
                    listItemsLock.unlock()
                } else {
                    arrivalsLoop.stop()
                }
            }
            .launchIn(viewModelScope + coroutineContext)

//        viewModelScope.launch(coroutineContext) {
//            val arrivalsLoop =
//                ArrivalsLoop(
//                    busServiceNumber = busServiceNumber,
//                    busStopCode = secondaryBusStopCode,
//                    getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
//                    dispatcher = dispatcherProvider.pool8
//                )
//
//            secondaryArrivalsLoop = arrivalsLoop
//
//            arrivalsLoop.start(viewModelScope)
//                .catch { throwable ->
//                    FirebaseCrashlytics.getInstance().recordException(throwable)
//                }
//                .filterNotNull()
//                .collect { arrivalsLoopData ->
//                    // check for busStopCode & busServiceNumber
//                    // to prevent pushing any dangling loop output to UI
//                    if (arrivalsLoopData.busStopCode == secondaryBusStopCode
//                        && arrivalsLoopData.busServiceNumber == busServiceNumber
//                    ) {
//                        if (!listItemsLock.tryLock()) return@collect
//                        updateToActive<BusRouteListItemData.BusRouteNode.Next>(
//                            arrivalsLoopData.busArrival,
//                            secondaryBusStopCode
//                        )
//                        updateToActive<BusRouteListItemData.BusRouteNode.Previous>(
//                            arrivalsLoopData.busArrival,
//                            secondaryBusStopCode
//                        )
//                        listItemsLock.unlock()
//                    } else {
//                        arrivalsLoop.stop()
//                    }
//                }
//
//        }
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