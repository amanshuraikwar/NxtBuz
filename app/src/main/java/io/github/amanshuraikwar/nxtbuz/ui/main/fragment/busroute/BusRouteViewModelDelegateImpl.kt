package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busroute.model.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.*
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.ScreenState
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.domain.BusArrivalUpdate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.loop.ArrivalsLoop
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.loop.ArrivalsLoopData
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.ArrivingBusMapMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapEvent
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapUpdate
import io.github.amanshuraikwar.nxtbuz.util.TimeUtil
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import io.github.amanshuraikwar.nxtbuz.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.util.post
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Named

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class BusRouteViewModelDelegateImpl @Inject constructor(
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusRouteUseCase: GetBusRouteUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    @Named("collapseBottomSheet") private val _collapseBottomSheet: MutableLiveData<Unit>,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val mapUtil: MapUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BusRouteViewModelDelegate {

    private lateinit var curBusRouteState: ScreenState.BusRouteState
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onBusStopClicked: (BusStop) -> Unit
    private var mapStateId: Int = 0

    private val _primaryBusArrivalUpdate = MutableLiveData<BusArrivalUpdate>()
    override val primaryBusArrivalUpdate = _primaryBusArrivalUpdate

    private val _secondaryBusArrivalUpdate = MutableLiveData<BusArrivalUpdate>()
    override val secondaryBusArrivalUpdate = _secondaryBusArrivalUpdate

    private val _previousBusArrivalItems = MutableLiveData<List<BusRoutePreviousItem>>()
    override val previousBusStopItems = _previousBusArrivalItems.asEvent()

    private val _hidePreviousBusStopItems = MutableLiveData<BusRoutePreviousAllItem>()
    override val hidePreviousBusStopItems = _hidePreviousBusStopItems.asEvent()

    private val totalStops: Int
        get() = curBusRouteState.busRoute.busRouteNodeList.size

    private var primaryArrivalsLoop: ArrivalsLoop? = null
    private var secondaryArrivalsLoop: ArrivalsLoop? = null

    private val mapMarkerList = mutableListOf<ArrivingBusMapMarker>()

    fun stop(busRouteState: ScreenState.BusRouteState) {
        if (busRouteState == curBusRouteState) {
            primaryArrivalsLoop?.stop()
            secondaryArrivalsLoop?.stop()
        }
    }

    suspend fun start(
        busRouteState: ScreenState.BusRouteState,
        coroutineScope: CoroutineScope,
        onBusStopClicked: (BusStop) -> Unit,
        onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit
    ) = coroutineScope.launch(dispatcherProvider.io) {

        viewModelScope = coroutineScope
        this@BusRouteViewModelDelegateImpl.onBusStopClicked = onBusStopClicked

        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_arrivals_loading_128,
                "Gathering bus route info..."
            )
        )

        _collapseBottomSheet.post()

        curBusRouteState = busRouteState

        mapMarkerList.clear()

        mapStateId = mapViewModelDelegate.newState()

        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.ClearMap
        )

        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.AddMapMarkers(
                listOf(
                    MapMarker(
                        curBusRouteState.busStop.code,
                        curBusRouteState.busStop.latitude,
                        curBusRouteState.busStop.longitude,
                        R.drawable.ic_marker_bus_stop_48,
                        curBusRouteState.busStop.description
                    )
                )
            )
        )

        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.MoveCenter(
                curBusRouteState.busStop.latitude,
                curBusRouteState.busStop.longitude
            )
        )

        val busRoute = getBusRouteUseCase(
            busServiceNumber = curBusRouteState.busServiceNumber,
            busStopCode = curBusRouteState.busStop.code
        )

        curBusRouteState.busRoute = busRoute

        val currentBusRouteNodeIndex =
            busRoute.busRouteNodeList.indexOfFirst {
                it.busStopCode == curBusRouteState.busStop.code
            }

        if (currentBusRouteNodeIndex == -1) {
            throw Exception(
                "Current bus stop code " +
                        "${curBusRouteState.busStop.code} for service " +
                        "${curBusRouteState.busServiceNumber} is -1."
            )
        }

        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.AddRoute(
                mapUtil.getRouteLineColorLight(),
                mapUtil.getRouteLineWidthSmall(),
                busRoute.busRouteNodeList
                    .subList(0, currentBusRouteNodeIndex + 1)
                    .map { busRouteNode ->
                        busRouteNode.busStopLat to busRouteNode.busStopLng
                    }
            )
        )

        mapViewModelDelegate.pushMapEvent(
            mapStateId,
            MapEvent.AddRoute(
                mapUtil.getRouteLineColor(),
                mapUtil.getRouteLineWidth(),
                busRoute.busRouteNodeList
                    .subList(currentBusRouteNodeIndex, busRoute.busRouteNodeList.size)
                    .map { busRouteNode ->
                        busRouteNode.busStopLat to busRouteNode.busStopLng
                    }
            )
        )

        // add route to bottom sheet

        val lastBusStopSequence = busRoute.busRouteNodeList
            .maxBy { it.stopSequence }
            ?.stopSequence
            ?: throw Exception("Could not find max bus stop sequence for route $busRoute.")

        val totalDistance =
            busRoute
                .busRouteNodeList
                .find { it.stopSequence == lastBusStopSequence }
                ?.distance
                ?: throw Exception("No bus route node found for stop sequence $lastBusStopSequence.")

        val listItems = mutableListOf<RecyclerViewListItem>()

        listItems.add(
            BusRouteHeaderItem(
                busStopCode = curBusRouteState.busStop.code,
                busServiceNumber = curBusRouteState.busServiceNumber,
                totalBusStops = totalStops,
                totalDistance = totalDistance,
                originBusStopDescription = busRoute.originBusStopDescription,
                destinationBusStopDescription = busRoute.destinationBusStopDescription,
                starred = busRoute.starred,
                onStarToggle = onStarToggle
            )
        )

        val currentSequenceNumber: Int = curBusRouteState.busRoute.busRouteNodeList
            .find {
                it.busStopCode == curBusRouteState.busStop.code
            }
            ?.stopSequence
            ?: throw Exception(
                "Current bus stop ${curBusRouteState.busStop.code} is not in the bus route."
            )

        listItems.add(
            HeaderItem("Bus Stops") {
                if (currentSequenceNumber > 1) {
                    _hidePreviousBusStopItems.postValue(
                        BusRoutePreviousAllItem(
                            "",
                            "See previous ${currentSequenceNumber - 1} bus stops",
                            BusRouteItem.Position.MIDDLE,
                            ::onPreviousAllClicked
                        )
                    )
                }
            }
        )

        if (currentSequenceNumber > 1) {
            listItems.add(
                BusRoutePreviousAllItem(
                    "",
                    "See all ${currentSequenceNumber - 1} bus stops",
                    BusRouteItem.Position.MIDDLE,
                    ::onPreviousAllClicked
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
                            ::goToBusStop
                        )
                    }
                    busRouteNode.stopSequence > currentSequenceNumber -> {
                        BusRouteNextItem(
                            busRouteNode.busStopCode,
                            busRouteNode.busStopDescription,
                            when (busRouteNode.stopSequence) {
                                0 -> BusRouteItem.Position.ORIGIN
                                lastBusStopSequence -> BusRouteItem.Position.DESTINATION
                                else -> BusRouteItem.Position.MIDDLE
                            },
                            onGoToBusStopClick = ::goToBusStop,
                            onClick = ::startSecondaryArrivals
                        )
                    }
                    else -> return@forEachIndexed
                }
            )
        }

        _listItems.postValue(listItems)

        _loading.postValue(Loading.Hide)

        startStarredBusArrivalsLoop()
    }

    private fun startSecondaryArrivals(busStopCode: String) {
        viewModelScope.launch(dispatcherProvider.computation) {
            secondaryArrivalsLoop?.stop()

            val arrivalsLoop = ArrivalsLoop(
                busServiceNumber = curBusRouteState.busServiceNumber,
                busStopCode = busStopCode,
                getBusBusArrivalsUseCase = getBusBusArrivalsUseCase,
                dispatcher = dispatcherProvider.pool8
            )

            secondaryArrivalsLoop = arrivalsLoop

            arrivalsLoop.start(viewModelScope)
                .catch { throwable ->
                    FirebaseCrashlytics.getInstance().recordException(throwable)
                }
                .filterNotNull()
                .collect(
                    object : FlowCollector<ArrivalsLoopData> {
                        override suspend fun emit(value: ArrivalsLoopData) {
                            delay(300)
                            // check for busStopCode & busServiceNumber
                            // to prevent pushing any dangling loop output to UI
                            if (value.busStopCode == busStopCode
                                && value.busServiceNumber == curBusRouteState.busServiceNumber
                            ) {
                                handleSecondaryArrivals(busStopCode, value.busArrival)
                            } else {
                                arrivalsLoop.stop()
                            }
                        }
                    }
                )
        }
    }

    private fun handleSecondaryArrivals(busStopCode: String, busArrival: BusArrival) {

        if (busArrival.arrivals is Arrivals.Arriving) {

            val arrivalStrList = mutableListOf(
                busArrival.arrivals.nextArrivingBus.arrival
            )

            for (arrival in busArrival.arrivals.followingArrivingBusList) {
                arrivalStrList.add(arrival.arrival)
            }

            _secondaryBusArrivalUpdate.postValue(
                BusArrivalUpdate(
                    busStopCode, arrivalStrList, TimeUtil.currentTimeStr()
                )
            )

        } else {

            _secondaryBusArrivalUpdate.postValue(
                BusArrivalUpdate(
                    busStopCode,
                    listOf(
                        if (busArrival.arrivals is Arrivals.DataNotAvailable)
                            "No data"
                        else
                            "Not operating"
                    ),
                    TimeUtil.currentTimeStr()
                )
            )
        }
    }

    private suspend fun startStarredBusArrivalsLoop() {

        primaryArrivalsLoop?.stop()

        val arrivalsLoop = ArrivalsLoop(
            busServiceNumber = curBusRouteState.busServiceNumber,
            busStopCode = curBusRouteState.busStop.code,
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
            .collect(
                object : FlowCollector<ArrivalsLoopData> {
                    override suspend fun emit(value: ArrivalsLoopData) {
                        // check for busStopCode & busServiceNumber
                        // to prevent pushing any dangling loop output to UI
                        if (value.busStopCode == curBusRouteState.busStop.code
                            && value.busServiceNumber == curBusRouteState.busServiceNumber
                        ) {
                            handlePrimaryArrivals(value.busArrival)
                        } else {
                            arrivalsLoop.stop()
                        }
                    }
                }
            )

    }

    private suspend fun handlePrimaryArrivals(busArrival: BusArrival) {
        val busAddList = mutableListOf<MapMarker>()
        val busDeleteList = mutableListOf<String>()
        val busUpdateList = mutableListOf<MapUpdate>()

        if (busArrival.arrivals is Arrivals.Arriving) {

            val arrivalStrList = mutableListOf(
                busArrival.arrivals.nextArrivingBus.arrival
            )

            for (arrival in busArrival.arrivals.followingArrivingBusList) {
                arrivalStrList.add(arrival.arrival)
            }

            _primaryBusArrivalUpdate.postValue(
                BusArrivalUpdate(
                    curBusRouteState.busStop.code, arrivalStrList, TimeUtil.currentTimeStr()
                )
            )

            var curIndex = 0

            if (curIndex >= mapMarkerList.size) {

                val mapMarker = ArrivingBusMapMarker(
                    busArrival.serviceNumber,
                    busArrival.arrivals.nextArrivingBus.latitude,
                    busArrival.arrivals.nextArrivingBus.longitude,
                    if (busArrival.arrivals.nextArrivingBus.arrival == "Arr") {
                        "ARRIVING"
                    } else {
                        "${busArrival.arrivals.nextArrivingBus.arrival} MINS"
                    },
                    busArrival.serviceNumber,
                )
                mapMarkerList.add(mapMarker)
                busAddList.add(mapMarker)
            } else {
                if (busArrival.arrivals.nextArrivingBus.latitude != mapMarkerList[curIndex].lat
                    || busArrival.arrivals.nextArrivingBus.longitude != mapMarkerList[curIndex].lng
                ) {
                    busUpdateList.add(
                        MapUpdate(
                            id = mapMarkerList[curIndex].id,
                            newLat = busArrival.arrivals.nextArrivingBus.latitude,
                            newLng = busArrival.arrivals.nextArrivingBus.longitude
                        )
                    )
                    mapMarkerList[curIndex] = mapMarkerList[curIndex].copy(
                        newLat = busArrival.arrivals.nextArrivingBus.latitude,
                        newLng = busArrival.arrivals.nextArrivingBus.longitude,
                        newDescription = "",
                    )
                }
            }

            curIndex++

            busArrival.arrivals.followingArrivingBusList.forEach { arrivingBus ->
                if (curIndex >= mapMarkerList.size) {

                    val mapMarker = ArrivingBusMapMarker(
                        busArrival.serviceNumber,
                        arrivingBus.latitude,
                        arrivingBus.longitude,
                        if (arrivingBus.arrival == "Arr") {
                            "ARRIVING"
                        } else {
                            "${arrivingBus.arrival} MINS"
                        },
                        busArrival.serviceNumber,
                    )
                    mapMarkerList.add(mapMarker)
                    busAddList.add(mapMarker)

                } else {
                    if (arrivingBus.latitude != mapMarkerList[curIndex].lat
                        || arrivingBus.longitude != mapMarkerList[curIndex].lng
                    ) {
                        busUpdateList.add(
                            MapUpdate(
                                id = mapMarkerList[curIndex].id,
                                newLat = arrivingBus.latitude,
                                newLng = arrivingBus.longitude
                            )
                        )
                        mapMarkerList[curIndex] = mapMarkerList[curIndex].copy(
                            newLat = arrivingBus.latitude,
                            newLng = arrivingBus.longitude,
                            newDescription = "",
                        )
                    }
                }
                curIndex++
            }

            while (curIndex < mapMarkerList.size) {
                busDeleteList.add(
                    mapMarkerList[curIndex].id
                )
                mapMarkerList.removeAt(curIndex)
            }

        } else {

            _primaryBusArrivalUpdate.postValue(
                BusArrivalUpdate(
                    curBusRouteState.busStop.code,
                    listOf(
                        if (busArrival.arrivals is Arrivals.DataNotAvailable)
                            "No data"
                        else
                            "Not operating"
                    ),
                    TimeUtil.currentTimeStr()
                )
            )

            // delete all the existing markers
            mapMarkerList.forEach { mapMarker ->
                busDeleteList.add(mapMarker.id)
            }

            mapMarkerList.clear()
        }

        withContext(dispatcherProvider.main) {
            if (busAddList.isNotEmpty()) {
                mapViewModelDelegate.pushMapEvent(
                    mapStateId,
                    MapEvent.AddMapMarkers(
                        busAddList
                    )
                )
            }
            if (busDeleteList.isNotEmpty()) {
                mapViewModelDelegate.pushMapEvent(
                    mapStateId,
                    MapEvent.DeleteMarker(
                        busDeleteList
                    )
                )
            }
            if (busUpdateList.isNotEmpty()) {
                mapViewModelDelegate.pushMapEvent(
                    mapStateId,
                    MapEvent.UpdateMapMarkers(
                        busUpdateList
                    )
                )
            }
        }

        Log.i(TAG, "startArrivalsLoop: add ${busAddList.size}")
        Log.i(TAG, "startArrivalsLoop: delete ${busDeleteList.size}")
        Log.i(TAG, "startArrivalsLoop: update ${busUpdateList.size}")
    }

    private fun goToBusStop(busStopCode: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            onBusStopClicked(getBusStopUseCase(busStopCode))
        }
    }

    private fun onPreviousAllClicked(@Suppress("UNUSED_PARAMETER") any: String) {
        val previousBusRouteNodeList = curBusRouteState
            .busRoute
            .busRouteNodeList.sortedBy { it.stopSequence }
            .subList(
                0,
                curBusRouteState
                    .busRoute
                    .busRouteNodeList
                    .indexOfFirst { it.busStopCode == curBusRouteState.busStop.code }
            )

        if (previousBusRouteNodeList.isEmpty()) return

        val lastBusStopSequence = curBusRouteState.busRoute.busRouteNodeList
            .maxBy { it.stopSequence }
            ?.stopSequence
            ?: throw Exception(
                "Could not find max bus stop sequence for route ${curBusRouteState.busRoute}."
            )

        val previousBusStopItemList: List<BusRoutePreviousItem> =
            previousBusRouteNodeList.map { busRouteNode ->
                BusRoutePreviousItem(
                    busRouteNode.busStopCode,
                    busRouteNode.busStopDescription,
                    when (busRouteNode.stopSequence) {
                        1 -> BusRouteItem.Position.ORIGIN
                        lastBusStopSequence -> BusRouteItem.Position.DESTINATION
                        else -> BusRouteItem.Position.MIDDLE
                    },
                    onGoToBusStopClick = ::goToBusStop,
                    onClick = ::startSecondaryArrivals,
                )
            }

        _previousBusArrivalItems.postValue(previousBusStopItemList)
    }

    override suspend fun onBottomSheetCollapsed() {
        // do nothing
    }

    companion object {
        private const val TAG = "BusRouteViewModelDelega"
    }
}