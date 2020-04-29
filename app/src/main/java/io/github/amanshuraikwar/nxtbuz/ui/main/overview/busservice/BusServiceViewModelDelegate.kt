package io.github.amanshuraikwar.nxtbuz.ui.main.overview.busservice

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.ui.list.BusArrivalCompactItem
import io.github.amanshuraikwar.nxtbuz.ui.list.BusArrivalErrorItem
import io.github.amanshuraikwar.nxtbuz.ui.list.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.ui.list.HeaderItem
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.model.MapEvent
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.model.MapMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.ScreenState
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.model.MapUpdate
import io.github.amanshuraikwar.nxtbuz.util.MapUtil
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class BusServiceViewModelDelegate @Inject constructor(
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusRouteUseCase: GetBusRouteUseCase,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val mapUtil: MapUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var curBusStopState: ScreenState.BusServiceState
    private lateinit var viewModelScope: CoroutineScope
    private lateinit var onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit
    private var arrivalsLoopJob: Job? = null

    private val arrivalsLoopErrorHandler = CoroutineExceptionHandler { _, _ ->
        Log.i(TAG, "arrivalsLoopErrorHandler: Exception thrown.")
        startStarredBusArrivalsLoopDelayed()
    }

    private val serviceNumberMapMarkerMap =
        mutableMapOf<String, MapMarker>()

    suspend fun start(
        busStopState: ScreenState.BusServiceState,
        coroutineScope: CoroutineScope
    ) = coroutineScope.launch(dispatcherProvider.io) {

        viewModelScope = coroutineScope

        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_arrivals_loading_128,
                "Gathering bus route info..."
            )
        )

        curBusStopState = busStopState

        serviceNumberMapMarkerMap.clear()

        mapViewModelDelegate.pushMapEvent(
            MapEvent.ClearMap
        )

        mapViewModelDelegate.pushMapEvent(
            MapEvent.AddMapMarkers(
                listOf(
                    MapMarker(
                        curBusStopState.busStop.code,
                        curBusStopState.busStop.latitude,
                        curBusStopState.busStop.longitude,
                        R.drawable.ic_marker_bus_stop_48,
                        curBusStopState.busStop.description
                    )
                )
            )
        )

        mapViewModelDelegate.pushMapEvent(
            MapEvent.MoveCenter(
                curBusStopState.busStop.latitude,
                curBusStopState.busStop.longitude
            )
        )

        val busRoute = getBusRouteUseCase(
            busServiceNumber = curBusStopState.busServiceNumber,
            busStopCode = curBusStopState.busStop.code
        )

        val currentBusRouteNodeIndex =
            busRoute.busRouteNodeList.indexOfFirst {
                it.busStopCode == curBusStopState.busStop.code
            }

        if (currentBusRouteNodeIndex == -1) {
            throw Exception(
                "Current bus stop code " +
                        "${curBusStopState.busStop.code} for service " +
                        "${curBusStopState.busServiceNumber} is -1."
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

        arrivalsLoopJob?.cancel()
        //startStarredBusArrivalsLoop()
    }

    private fun startStarredBusArrivalsLoop() {
        arrivalsLoopJob = viewModelScope.launch(arrivalsLoopErrorHandler) {
            startArrivalsLoop(curBusStopState.busStop, onStarToggle)
        }
    }

    private fun startStarredBusArrivalsLoopDelayed() {
        arrivalsLoopJob = viewModelScope.launch(arrivalsLoopErrorHandler) {
            startArrivalsLoop(curBusStopState.busStop, onStarToggle, REFRESH_DELAY)
        }
    }

    private suspend fun startArrivalsLoop(
        busStop: BusStop,
        onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit,
        initialDelay: Long = 0
    ) = withContext(dispatcherProvider.computation) {

    }

    companion object {
        private const val REFRESH_DELAY = 10000L
        private const val TAG = "BusServiceViewModelDele"
    }
}