package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busroute.model.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.BusRouteHeaderItem
import io.github.amanshuraikwar.nxtbuz.ui.list.BusRouteNodeItem
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.ScreenState
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.ArrivingBusMapMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapEvent
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapUpdate
import io.github.amanshuraikwar.nxtbuz.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.util.post
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

class BusRouteViewModelDelegate @Inject constructor(
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusRouteUseCase: GetBusRouteUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    @Named("collapseBottomSheet") private val _collapseBottomSheet: MutableLiveData<Unit>,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val mapUtil: MapUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var curBusRouteState: ScreenState.BusRouteState
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onBusStopClicked: (BusStop) -> Unit
    private var arrivalsLoopJob: Job? = null

    private val arrivalsLoopErrorHandler = CoroutineExceptionHandler { _, _ ->
        Log.i(TAG, "arrivalsLoopErrorHandler: Exception thrown.")
        startStarredBusArrivalsLoopDelayed()
    }

    private val mapMarkerList = mutableListOf<ArrivingBusMapMarker>()

    suspend fun stop(busRouteState: ScreenState.BusRouteState) {
        if (busRouteState == curBusRouteState) {
            arrivalsLoopJob?.cancel()
        }
    }

    suspend fun start(
        busRouteState: ScreenState.BusRouteState,
        coroutineScope: CoroutineScope,
        onBusStopClicked: (BusStop) -> Unit,
        onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit
    ) = coroutineScope.launch(dispatcherProvider.io) {

        viewModelScope = coroutineScope
        this@BusRouteViewModelDelegate.onBusStopClicked = onBusStopClicked

        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_arrivals_loading_128,
                "Gathering bus route info..."
            )
        )

        _collapseBottomSheet.post()

        curBusRouteState = busRouteState

        mapMarkerList.clear()

        mapViewModelDelegate.pushMapEvent(
            MapEvent.ClearMap
        )

        mapViewModelDelegate.pushMapEvent(
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
            MapEvent.MoveCenter(
                curBusRouteState.busStop.latitude,
                curBusRouteState.busStop.longitude
            )
        )

        val busRoute = getBusRouteUseCase(
            busServiceNumber = curBusRouteState.busServiceNumber,
            busStopCode = curBusRouteState.busStop.code
        )

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

        val listItems = mutableListOf<RecyclerViewListItem>()
        listItems.add(
            BusRouteHeaderItem(
                busStopCode = curBusRouteState.busStop.code,
                busServiceNumber = curBusRouteState.busServiceNumber,
                busStopDescription = curBusRouteState.busStop.description,
                originBusStopDescription = busRoute.originBusStopDescription,
                destinationBusStopDescription = busRoute.destinationBusStopDescription,
                starred = busRoute.starred,
                onStarToggle = onStarToggle
            )
        )

        busRoute.busRouteNodeList.forEachIndexed { index: Int, busRouteNode: BusRouteNode ->
            listItems.add(
                BusRouteNodeItem(
                    busRouteNode,
                    first = index == 0,
                    last = index == busRoute.busRouteNodeList.size - 1,
                    onBusStopClicked = ::onBusStopClicked
                )
            )
        }

        _listItems.postValue(listItems)

        _loading.postValue(Loading.Hide)

        arrivalsLoopJob?.cancel()
        startStarredBusArrivalsLoop()
    }

    private fun startStarredBusArrivalsLoop() {
        arrivalsLoopJob = viewModelScope.launch(arrivalsLoopErrorHandler) {
            startArrivalsLoop(curBusRouteState.busStop, curBusRouteState.busServiceNumber)
        }
    }

    private fun startStarredBusArrivalsLoopDelayed() {
        arrivalsLoopJob = viewModelScope.launch(arrivalsLoopErrorHandler) {
            startArrivalsLoop(
                curBusRouteState.busStop, curBusRouteState.busServiceNumber, REFRESH_DELAY
            )
        }
    }

    private suspend fun startArrivalsLoop(
        busStop: BusStop,
        busServiceNumber: String,
        initialDelay: Long = 0
    ) = withContext(dispatcherProvider.computation) {

        delay(initialDelay)

        while (isActive) {

            val busArrival =
                getBusBusArrivalsUseCase(
                    busStop.code, busServiceNumber
                )

            val busAddList = mutableListOf<MapMarker>()
            val busDeleteList = mutableListOf<String>()
            val busUpdateList = mutableListOf<MapUpdate>()

            if (busArrival.arrivals is Arrivals.Arriving) {

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

                // delete all the existing markers
                mapMarkerList.forEach { mapMarker ->
                    busDeleteList.add(mapMarker.id)
                }

                mapMarkerList.clear()
            }

            withContext(dispatcherProvider.main) {
                if (busAddList.isNotEmpty()) {
                    mapViewModelDelegate.pushMapEvent(
                        MapEvent.AddMapMarkers(
                            busAddList
                        )
                    )
                }
                if (busDeleteList.isNotEmpty()) {
                    mapViewModelDelegate.pushMapEvent(
                        MapEvent.DeleteMarker(
                            busDeleteList
                        )
                    )
                }
                if (busUpdateList.isNotEmpty()) {
                    mapViewModelDelegate.pushMapEvent(
                        MapEvent.UpdateMapMarkers(
                            busUpdateList
                        )
                    )
                }
            }

            Log.i(TAG, "startArrivalsLoop: add ${busAddList.size}")
            Log.i(TAG, "startArrivalsLoop: delete ${busDeleteList.size}")
            Log.i(TAG, "startArrivalsLoop: update ${busUpdateList.size}")

            delay(REFRESH_DELAY)
        }
    }

    private fun onBusStopClicked(busStopCode: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            onBusStopClicked(getBusStopUseCase(busStopCode))
        }
    }

    companion object {
        private const val REFRESH_DELAY = 10000L
        private const val TAG = "BusRouteViewModelDelega"
    }
}