package io.github.amanshuraikwar.nxtbuz.busroute.ui

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busroute.R
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteHeaderData
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteScreenState
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
import io.github.amanshuraikwar.nxtbuz.common.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRoute
import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.BusServiceArrivalsLoop
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
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

private const val TAG = "BusRouteViewModel"

class BusRouteViewModel @Inject constructor(
    private val getBusRouteUseCase: GetBusRouteUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val isStarredUseCase: IsBusServiceStarredUseCase,
    private val toggleStar: ToggleBusServiceStarUseCase,
    private val toggleStarUpdateUseCase: ToggleStarUpdateUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val mapUtil: MapUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed()
    }

    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private var listItems = SnapshotStateList<BusRouteListItemData>()
    lateinit var currentBusStop: BusStop
    lateinit var busRoute: BusRoute
    lateinit var busServiceNumber: String

    private var primaryBusServiceArrivalsLoop: BusServiceArrivalsLoop? = null
    private var secondaryBusServiceArrivalsLoop: BusServiceArrivalsLoop? = null
    private val listItemsLock = Mutex()
    internal var bottomSheetInit = false

    private val _screenState =
        MutableStateFlow<BusRouteScreenState>(BusRouteScreenState.Fetching)
    val screenState: StateFlow<BusRouteScreenState> = _screenState

    private var starred = MutableStateFlow(false)
    private var listenStarUpdatesJob: Job? = null

    fun init(busServiceNumber: String, busStopCode: String) {
        FirebaseCrashlytics.getInstance().setCustomKey(
            "viewModel", "$TAG-$busStopCode-$busServiceNumber"
        )

        viewModelScope.launch(coroutineContext) {
            _screenState.emit(BusRouteScreenState.Fetching)

            val busStop = getBusStopUseCase(busStopCode) ?: return@launch

            listItemsLock.withLock {
                addBusStopMapMarker(busStop = busStop)

                this@BusRouteViewModel.busServiceNumber = busServiceNumber
                this@BusRouteViewModel.currentBusStop = busStop

                busRoute = getBusRouteUseCase(
                    busServiceNumber = this@BusRouteViewModel.busServiceNumber,
                    busStopCode = this@BusRouteViewModel.currentBusStop.code
                )

                listItems = SnapshotStateList()

                starred = MutableStateFlow(
                    isStarredUseCase(
                        busStopCode = currentBusStop.code,
                        busServiceNumber = busServiceNumber
                    )
                )

                _screenState.emit(
                    BusRouteScreenState.Success(
                        header = BusRouteHeaderData(
                            busServiceNumber = busServiceNumber,
                            destinationBusStopDescription = busRoute.destinationBusStopDescription,
                            originBusStopDescription = busRoute.originBusStopDescription,
                            busStopCode = currentBusStop.code,
                            starred = starred
                        ),
                        listItems = listItems
                    )
                )

                listenToggleStarUpdate()
                waitForBottomSheetInit()
                pushBusRouteListItems()
                startPrimaryBusArrivalsLoop()

                addBusRouteToMap(
                    busRoute,
                    busStopCode = this@BusRouteViewModel.currentBusStop.code
                )
            }
        }
    }

    private suspend fun waitForBottomSheetInit() {
        while (true) {
            if (bottomSheetInit) break
            delay(300)
        }
    }

    private suspend fun addBusRouteToMap(busRoute: BusRoute, busStopCode: String) {
        withContext(dispatcherProvider.computation) {
            pushMapEventUseCase(
                MapEvent.AddRoute(
                    "${busRoute.busServiceNumber}_at_$busStopCode",
                    mapUtil.getRouteLineColor(),
                    mapUtil.getRouteLineWidth(),
                    busRoute.busRouteNodeList
                        .map { busRouteNode ->
                            busRouteNode.busStopLat to busRouteNode.busStopLng
                        }
                )
            )
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
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        listenStarUpdatesJob = viewModelScope.launch(coroutineContext) {
            toggleStarUpdateUseCase()
                .collect { toggleStarUpdate ->
                    if (busServiceNumber == toggleStarUpdate.busServiceNumber
                        && currentBusStop.code == toggleStarUpdate.busStopCode
                    ) {
                        starred.value = toggleStarUpdate.newStarState
                    }
                }
        }
    }

    private fun pushBusRouteListItems() {
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
            BusRouteListItemData.Header(
                id = "bus-route-${currentBusStop.code}-$busServiceNumber-stops-header",
                title = "Stops"
            )
        )

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
                    id = "previous-all-" +
                            "${currentBusStop.code}-" +
                            "$busServiceNumber-" +
                            "${currentSequenceNumber - 1}",
                    title = "See previous ${currentSequenceNumber - 1} bus stops"
                )
            )
        }

        busRoute.busRouteNodeList.forEachIndexed { _, busRouteNode: BusRouteNode ->
            listItems.add(
                when {
                    busRouteNode.stopSequence == currentSequenceNumber -> {
                        BusRouteListItemData.BusRouteNode.Current(
                            id = "node-" +
                                    "${busRouteNode.busStopCode}-" +
                                    "$busServiceNumber-" +
                                    "${busRouteNode.stopSequence}",
                            busStopCode = busRouteNode.busStopCode,
                            busStopDescription = busRouteNode.busStopDescription,
                            position = busRouteNode.stopSequence.toPosition(lastBusStopSequence),
                        )
                    }
                    busRouteNode.stopSequence > currentSequenceNumber -> {
                        BusRouteListItemData.BusRouteNode.Next(
                            id = "node-" +
                                    "${busRouteNode.busStopCode}-" +
                                    "$busServiceNumber-" +
                                    "${busRouteNode.stopSequence}",
                            busStopCode = busRouteNode.busStopCode,
                            busStopDescription = busRouteNode.busStopDescription,
                            position = busRouteNode.stopSequence.toPosition(lastBusStopSequence)
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
            if (!listItemsLock.tryLock()) return@launch

            val previousBusRouteNodeList = busRoute
                .busRouteNodeList.sortedBy { it.stopSequence }
                .subList(
                    0,
                    busRoute.busRouteNodeList.indexOfFirst { it.busStopCode == currentBusStop.code }
                )

            if (previousBusRouteNodeList.isEmpty()) return@launch

            val lastBusStopSequence = busRoute.busRouteNodeList
                .maxByOrNull { it.stopSequence }
                ?.stopSequence
                ?: throw Exception(
                    "Could not find max bus stop sequence for route $busRoute."
                )

            val previousBusStopItemList: List<BusRouteListItemData.BusRouteNode.Previous> =
                previousBusRouteNodeList.map { busRouteNode ->
                    Log.i(TAG, "previousAllClicked: ${busRouteNode.stopSequence}")
                    BusRouteListItemData.BusRouteNode.Previous(
                        busStopCode = busRouteNode.busStopCode,
                        busStopDescription = busRouteNode.busStopDescription,
                        position = when (busRouteNode.stopSequence) {
                            previousBusRouteNodeList[0].stopSequence ->
                                BusRouteListItemData.BusRouteNode.Position.ORIGIN
                            lastBusStopSequence ->
                                BusRouteListItemData.BusRouteNode.Position.DESTINATION
                            else -> BusRouteListItemData.BusRouteNode.Position.MIDDLE
                        },
                        id = "node-" +
                                "${busRouteNode.busStopCode}-" +
                                "$busServiceNumber-" +
                                "${busRouteNode.stopSequence}",
                    )
                }

            val previousAllIndex =
                listItems.indexOfFirst { it is BusRouteListItemData.BusRoutePreviousAll }

            if (previousAllIndex != -1) {
                listItems.removeAt(previousAllIndex)
                listItems.addAll(previousAllIndex, previousBusStopItemList)
            }

            listItemsLock.unlock()
        }
    }

    @Synchronized
    private fun startPrimaryBusArrivalsLoop() {
        primaryBusServiceArrivalsLoop?.stop()
        primaryBusServiceArrivalsLoop = null

        primaryBusServiceArrivalsLoop =
            BusServiceArrivalsLoop(
                busServiceNumber = busServiceNumber,
                busStopCode = currentBusStop.code,
                getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
                dispatcher = dispatcherProvider.arrivalService,
                coroutineScope = viewModelScope
            )

        primaryBusServiceArrivalsLoop?.startAndCollect(
            coroutineContext = coroutineContext
        ) collect@{ arrivalsLoopData ->
            // check for busStopCode & busServiceNumber
            // to prevent pushing any dangling loop output to UI
            if (arrivalsLoopData.busStopCode == currentBusStop.code
                && arrivalsLoopData.busServiceNumber == busServiceNumber
            ) {
                if (!listItemsLock.tryLock()) return@collect
                updateToActive<BusRouteListItemData.BusRouteNode.Current>(
                    arrivalsLoopData.busStopArrival,
                    currentBusStop.code
                )
                listItemsLock.unlock()
            } else {
                primaryBusServiceArrivalsLoop?.stop()
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

    private inline fun <reified T : BusRouteListItemData.BusRouteNode> updateToActive(
        busStopArrival: BusStopArrival,
        busStopCode: String,
    ): Boolean {
        val temp: Pair<Int, T> =
            listItems.find { it.busStopCode == busStopCode } ?: return false

        val (listItemIndex, currentListItemData) = temp

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Current) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Active(
                    busArrivals = busStopArrival.busArrivals,
                    lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}"
                )
            )
            return true
        }

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Next) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Active(
                    busArrivals = busStopArrival.busArrivals,
                    lastUpdatedOn = "Last updated on ${TimeUtil.currentTimeStr()}"
                )
            )
            return true
        }

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Previous) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Active(
                    busArrivals = busStopArrival.busArrivals,
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

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Previous) {
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

        if (currentListItemData is BusRouteListItemData.BusRouteNode.Previous) {
            listItems[listItemIndex] = currentListItemData.copy(
                arrivalState = BusRouteListItemData.ArrivalState.Inactive
            )
            return true
        }

        return false
    }

    private fun failed() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(
                BusRouteScreenState.Failed(null)
            )
        }
    }

    fun onExpand(expandingBusStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            if (!(updateToFetching<BusRouteListItemData.BusRouteNode.Next>(
                    expandingBusStopCode
                )
                        || updateToFetching<BusRouteListItemData.BusRouteNode.Previous>(
                    expandingBusStopCode
                ))
            ) {
                return@launch
            }

            listItemsLock.withLock {
                secondaryBusServiceArrivalsLoop?.busStopCode?.let { currentSecondaryBusStopCode ->
                    updateToInactive<BusRouteListItemData.BusRouteNode.Next>(
                        currentSecondaryBusStopCode
                    )
                            || updateToInactive<BusRouteListItemData.BusRouteNode.Previous>(
                        currentSecondaryBusStopCode
                    )
                }

                startSecondaryArrivals(expandingBusStopCode)
            }
        }
    }

    fun onCollapse(collapsingBusStopCode: String) {
        viewModelScope.launch(coroutineContext) {
            if (collapsingBusStopCode == secondaryBusServiceArrivalsLoop?.busStopCode) {
                if (!listItemsLock.tryLock()) return@launch

                secondaryBusServiceArrivalsLoop?.stop()

                updateToInactive<BusRouteListItemData.BusRouteNode.Next>(
                    collapsingBusStopCode
                )
                updateToInactive<BusRouteListItemData.BusRouteNode.Previous>(
                    collapsingBusStopCode
                )

                secondaryBusServiceArrivalsLoop = null

                listItemsLock.unlock()
            }
        }
    }

    private fun startSecondaryArrivals(secondaryBusStopCode: String) {
        secondaryBusServiceArrivalsLoop?.stop()
        secondaryBusServiceArrivalsLoop = null

        secondaryBusServiceArrivalsLoop =
            BusServiceArrivalsLoop(
                busServiceNumber = busServiceNumber,
                busStopCode = secondaryBusStopCode,
                getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
                dispatcher = dispatcherProvider.arrivalService,
                coroutineScope = viewModelScope
            )

        secondaryBusServiceArrivalsLoop?.startAndCollect(
            coroutineContext = coroutineContext
        ) collect@{ arrivalsLoopData ->
            // check for busStopCode & busServiceNumber
            // to prevent pushing any dangling loop output to UI
            if (arrivalsLoopData.busStopCode == secondaryBusStopCode
                && arrivalsLoopData.busServiceNumber == busServiceNumber
            ) {
                if (!listItemsLock.tryLock()) return@collect
                updateToActive<BusRouteListItemData.BusRouteNode.Next>(
                    arrivalsLoopData.busStopArrival,
                    secondaryBusStopCode
                )
                updateToActive<BusRouteListItemData.BusRouteNode.Previous>(
                    arrivalsLoopData.busStopArrival,
                    secondaryBusStopCode
                )
                listItemsLock.unlock()
            } else {
                secondaryBusServiceArrivalsLoop?.stop()
            }
        }
    }

    fun onStarToggle(busServiceNumber: String, busStopCode: String, newValue: Boolean) {
        viewModelScope.launch(coroutineContext) {
            if (busServiceNumber == this@BusRouteViewModel.busServiceNumber &&
                busStopCode == this@BusRouteViewModel.currentBusStop.code
            ) {
                starred.value = newValue
                toggleStar(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber,
                    toggleTo = newValue
                )
            }
        }
    }

    fun onDispose() {
        pushMapEventUseCase(
            MapEvent.DeleteMarker(
                markerId = currentBusStop.code,
            )
        )

        pushMapEventUseCase(
            MapEvent.DeleteRoute(
                "${busRoute.busServiceNumber}_at_${currentBusStop.code}",
            )
        )

        primaryBusServiceArrivalsLoop?.stop()
        primaryBusServiceArrivalsLoop = null
        secondaryBusServiceArrivalsLoop?.stop()
        secondaryBusServiceArrivalsLoop = null
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        bottomSheetInit = false
    }
}