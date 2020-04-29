package io.github.amanshuraikwar.nxtbuz.ui.main.overview.busstop

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
import javax.inject.Inject
import javax.inject.Named

class BusStopViewModelDelegate @Inject constructor(
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("listItems") private val _listItems: MutableLiveData<List<RecyclerViewListItem>>,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private lateinit var curBusStopState: ScreenState.BusStopState
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
        busStopState: ScreenState.BusStopState,
        onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit,
        coroutineScope: CoroutineScope
    ) = coroutineScope.launch(dispatcherProvider.io) {
        viewModelScope = coroutineScope
        this@BusStopViewModelDelegate.onStarToggle = onStarToggle
        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_arrivals_loading_128,
                "Finding bus arrivals..."
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
        arrivalsLoopJob?.cancel()
        startStarredBusArrivalsLoop()
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

        delay(initialDelay)

        while (isActive) {

            val busArrivals = getBusBusArrivalsUseCase(busStop.code)

            val listItems: MutableList<RecyclerViewListItem> =
                busArrivals
                    .map {
                        if (it.arrivals is Arrivals.Arriving) {
                            BusArrivalCompactItem(
                                busStop.code,
                                it,
                                onStarToggle
                            )
                        } else {
                            BusArrivalErrorItem(
                                busStop.code,
                                it,
                                onStarToggle
                            )
                        }
                    }
                    .toMutableList()

            listItems.add(
                0,
                BusStopHeaderItem(
                    busStop,
                    R.drawable.ic_bus_stop_24
                )
            )

            listItems.add(
                1,
                HeaderItem(
                    "Arrivals"
                )
            )

            if (isActive) {

                _listItems.postValue(listItems)

                val busAddList = mutableListOf<MapMarker>()
                val busDeleteList = mutableListOf<String>()
                val busUpdateList = mutableListOf<MapUpdate>()

                busArrivals
                    .forEach { busArrival ->
                        when (busArrival.arrivals) {
                            is Arrivals.Arriving -> {
                                serviceNumberMapMarkerMap[busArrival.serviceNumber]
                                    ?.let { mapMarker ->
                                        if (busArrival.arrivals.nextArrivingBus.latitude != mapMarker.lat
                                            || busArrival.arrivals.nextArrivingBus.longitude != mapMarker.lng
                                        ) {
                                            busUpdateList.add(
                                                MapUpdate(
                                                    mapMarker.id,
                                                    busArrival.arrivals.nextArrivingBus.latitude,
                                                    busArrival.arrivals.nextArrivingBus.longitude
                                                )
                                            )
                                            serviceNumberMapMarkerMap[busArrival.serviceNumber] =
                                                mapMarker.copy(
                                                    lat = busArrival.arrivals.nextArrivingBus.latitude,
                                                    lng = busArrival.arrivals.nextArrivingBus.longitude
                                                )
                                        }
                                    }
                                    ?: run {
                                        val mapMarker = MapMarker(
                                            busArrival.serviceNumber,
                                            busArrival.arrivals.nextArrivingBus.latitude,
                                            busArrival.arrivals.nextArrivingBus.longitude,
                                            R.drawable.ic_marker_arriving_bus_48,
                                            if ((busArrival.arrivals).nextArrivingBus.arrival == "Arr") {
                                                "ARRIVING"
                                            } else {
                                                "${(busArrival.arrivals).nextArrivingBus.arrival} MINS"
                                            }
                                        )
                                        serviceNumberMapMarkerMap[busArrival.serviceNumber] =
                                            mapMarker
                                        busAddList.add(mapMarker)
                                    }
                            }
                            is Arrivals.DataNotAvailable,
                            is Arrivals.NotOperating -> {
                                serviceNumberMapMarkerMap[busArrival.serviceNumber]
                                    ?.let { mapMarker ->
                                        busDeleteList.add(mapMarker.id)
                                        serviceNumberMapMarkerMap.remove(mapMarker.id)
                                    }
                                    ?: run {
                                        // just ignore
                                    }
                            }
                        }
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
            }

            _loading.postValue(Loading.Hide)
            delay(REFRESH_DELAY)
        }
    }

    companion object {
        private const val REFRESH_DELAY = 10000L
        private const val TAG = "BusStopViewModelDelegat"
    }
}