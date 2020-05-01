package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.old

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.MarkerOptions
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetStarredBusStopsArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.MaxDistanceOfClosesBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.model.LocationOutput
import io.github.amanshuraikwar.nxtbuz.ui.list.*
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.Loading
import io.github.amanshuraikwar.nxtbuz.util.MapUtil
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "OverviewViewModel"

private const val REFRESH_DELAY = 10000L

class OverviewViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val maxDistanceOfClosesBusStopUseCase: MaxDistanceOfClosesBusStopUseCase,
    private val getBusBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val toggleBusStopStarUseCase: ToggleBusStopStarUseCase,
    private val getStarredBusStopsArrivalsUseCase: GetStarredBusStopsArrivalsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val mapUtil: MapUtil,
    @Named("onBackPressed") private val _onBackPressed: MutableLiveData<Unit>
) : ViewModel() {

    private var lastBusStop: BusStop? = null
    private var lastAppState: AppState? = null

    val onBackPressed = _onBackPressed.asEvent()

    private val _error = MutableLiveData<Alert>()
    val error = _error
        .map {
            Log.e(TAG, "onError: $it")
            it
        }
        .asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        _error.postValue(Alert())
    }

    private val starredBusErrorHandler = CoroutineExceptionHandler { _, _ ->
        startStarredBusArrivalsLoopDelayed()
    }

    private val arrivalsLoopErrorHandler = CoroutineExceptionHandler { _, _ ->
        startDelayedArrivalsLoop(lastBusStop ?: return@CoroutineExceptionHandler)
    }

    private val _listItems = MutableLiveData<Pair<MutableList<RecyclerViewListItem>, Boolean>>()
    val listItems = _listItems.map { it }

    private val _goto = MutableLiveData<BusStop>()
    val goto = _goto.asEvent()

    private val _loading = MutableLiveData<Loading>()
    val loading = _loading

    private val _locationStatus = MutableLiveData<Boolean>()
    val locationStatus = _locationStatus

    var mapReady = false

    private val _mapCenter = MutableLiveData<Pair<Double, Double>>()
    val mapCenter = _mapCenter

    private val _mapMarker = MutableLiveData<Pair<List<MarkerOptions>, Boolean>>()
    val mapMarker = _mapMarker

    private val _clearMap = MutableLiveData<Unit>()
    val clearMap = _clearMap.asEvent()

    private val _mapCircle = MutableLiveData<Pair<Pair<Double, Double>, Double>>()
    val mapCircle = _mapCircle

    private val _initMap = MutableLiveData<Pair<Double, Double>>()
    val initMap = _initMap

    private val _showBack = MutableLiveData<Boolean>()
    val showBack = _showBack

    private val _collapseBottomSheet = MutableLiveData<Unit>()
    val collapseBottomSheet = _collapseBottomSheet.asEvent()

    private val _starredListItems =
        MutableLiveData<MutableList<RecyclerViewListItem>>()
    val starredListItems = _starredListItems

    init {
        fetchDefaultLocationInit()
        fetchDataInit()
        startStarredBusArrivalsLoop()
    }

    private fun startStarredBusArrivalsLoopDelayed() {
        startStarredBusArrivalsLoop(REFRESH_DELAY)
    }

    private fun startStarredBusArrivalsLoop(initialDelay: Long = 0) =
        viewModelScope.launch(dispatcherProvider.io + starredBusErrorHandler) {
            delay(initialDelay)
            while (true) {
                _starredListItems.postValue(
                    getStarredBusStopsArrivalsUseCase()
                        .map {
                            if (it.arrivals is Arrivals.Arriving)
                                StarredBusArrivalItem(it)
                            else
                                StarredBusArrivalErrorItem(it)
                        }
                        .toMutableList()
                )
                delay(REFRESH_DELAY)
            }
        }

    private fun fetchDefaultLocationInit() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _initMap.postValue(defaultLocationUseCase())
        }

    fun fetchBusStopsForLatLon(lat: Double, lon: Double) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _showBack.postValue(false)
            _loading.postValue(
                Loading.Show(
                    R.drawable.avd_anim_nearby_bus_stops_loading_128,
                    "Finding bus stops nearby..."
                )
            )
            _mapCenter.postValue(lat to lon)
            val busStopList =
                getBusStopsUseCase(lat = lat, lon = lon, limit = busStopsQueryLimitUseCase())

            if (mapUtil.measureDistanceMetres(
                    lat,
                    lon,
                    busStopList.first().latitude,
                    busStopList.first().longitude
                ) > maxDistanceOfClosesBusStopUseCase()
            ) {
                _error.postValue(
                    Alert(
                        "You are too far away."
                    )
                )
                return@launch
            }

            val radius = mapUtil.measureDistanceMetres(
                lat,
                lon,
                busStopList.last().latitude,
                busStopList.last().longitude
            )
            _mapCircle.postValue(lat to lon to radius)
            val listItems = getListItems(busStopList)
            _listItems.postValue(listItems to false)
            val markerOptionsList = mapUtil.busStopsToMarkers(busStopList)
            _mapMarker.postValue(markerOptionsList to false)
            _loading.postValue(Loading.Hide)
            lastAppState =
                AppState(
                    listItems,
                    markerOptionsList,
                    lat,
                    lon,
                    radius
                )
        }

    fun onRecenterClicked() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            when (val locationOutput = getLocationUseCase()) {
                is LocationOutput.PermissionsNotGranted,
                LocationOutput.CouldNotGetLocation -> {
                    _locationStatus.postValue(false)
                }
                is LocationOutput.Success -> {
                    _showBack.postValue(false)
                    _locationStatus.postValue(true)
                    _mapCenter.postValue(
                        locationOutput.latitude to locationOutput.longitude
                    )
                    _loading.postValue(
                        Loading.Show(
                            R.drawable.avd_anim_nearby_bus_stops_loading_128,
                            "Finding bus stops nearby..."
                        )
                    )
                    val busStopList = getBusStopsUseCase(
                        lat = locationOutput.latitude,
                        lon = locationOutput.longitude,
                        limit = busStopsQueryLimitUseCase()
                    )
                    val radius = mapUtil.measureDistanceMetres(
                        locationOutput.latitude,
                        locationOutput.longitude,
                        busStopList.last().latitude,
                        busStopList.last().longitude
                    )
                    _mapCircle.postValue(
                        locationOutput.latitude to locationOutput.longitude to radius
                    )
                    val listItems = getListItems(busStopList)
                    _listItems.postValue(listItems to false)
                    val markerOptionsList = mapUtil.busStopsToMarkers(busStopList)
                    _mapMarker.postValue(markerOptionsList to false)
                    _loading.postValue(Loading.Hide)
                    lastAppState =
                        AppState(
                            listItems,
                            markerOptionsList,
                            locationOutput.latitude,
                            locationOutput.longitude,
                            radius
                        )
                }
            }
        }

    private fun fetchDataInit() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _showBack.postValue(false)
            _loading.postValue(
                Loading.Show(
                    R.drawable.avd_anim_nearby_bus_stops_loading_128,
                    "Finding bus stops nearby..."
                )
            )
            // wait for map to be ready
            while (!mapReady) {
                delay(1000)
            }
            when (val locationOutput = getLocationUseCase()) {
                is LocationOutput.PermissionsNotGranted,
                is LocationOutput.CouldNotGetLocation -> {
                    _locationStatus.postValue(false)
                    val (lat, lon) = defaultLocationUseCase()
                    _mapCenter.postValue(lat to lon)
                    val busStopList = getBusStopsUseCase(
                        lat = lat,
                        lon = lon,
                        limit = busStopsQueryLimitUseCase()
                    )
                    val radius = mapUtil.measureDistanceMetres(
                        lat,
                        lon,
                        busStopList.last().latitude,
                        busStopList.last().longitude
                    )
                    _mapCircle.postValue(lat to lon to radius)
                    val listItems = getListItems(busStopList)
                    _listItems.postValue(listItems to false)
                    val markerOptionsList = mapUtil.busStopsToMarkers(busStopList)
                    _mapMarker.postValue(markerOptionsList to false)
                    lastAppState =
                        AppState(
                            listItems,
                            markerOptionsList,
                            lat,
                            lon,
                            radius
                        )
                }
                is LocationOutput.Success -> {
                    _locationStatus.postValue(true)
                    _mapCenter.postValue(
                        locationOutput.latitude to locationOutput.longitude
                    )
                    val busStopList = getBusStopsUseCase(
                        lat = locationOutput.latitude,
                        lon = locationOutput.longitude,
                        limit = busStopsQueryLimitUseCase()
                    )
                    val radius = mapUtil.measureDistanceMetres(
                        locationOutput.latitude,
                        locationOutput.longitude,
                        busStopList.last().latitude,
                        busStopList.last().longitude
                    )
                    _mapCircle.postValue(
                        locationOutput.latitude to locationOutput.longitude to radius
                    )
                    val listItems = getListItems(busStopList)
                    _listItems.postValue(listItems to false)
                    val markerOptionsList = mapUtil.busStopsToMarkers(busStopList)
                    _mapMarker.postValue(markerOptionsList to false)
                    lastAppState =
                        AppState(
                            listItems,
                            markerOptionsList,
                            locationOutput.latitude,
                            locationOutput.longitude,
                            radius
                        )
                }
            }
            _loading.postValue(Loading.Hide)
        }

    private suspend fun getListItems(busStopList: List<BusStop>): MutableList<RecyclerViewListItem> =
        withContext(dispatcherProvider.computation) {
            val listItems = mutableListOf<RecyclerViewListItem>()
            busStopList.forEach {
                listItems.add(
                    BusStopItem(
                        it,
                        R.drawable.ic_bus_stop_24,
                        ::onBusStopClicked,
                        ::onGotoClicked
                    )
                )
            }
            listItems
        }

    private fun onBusStopClicked(busStop: BusStop) {
        busStopSelected(busStop)
    }

    private fun onGotoClicked(busStop: BusStop) {
        _goto.postValue(busStop)
    }

    fun busStopSelected(busStop: BusStop) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _collapseBottomSheet.postValue(Unit)
            _showBack.postValue(true)
            _loading.postValue(
                Loading.Show(
                    R.drawable.avd_anim_arrivals_loading_128,
                    "Finding bus arrivals..."
                )
            )
            _mapCenter.postValue(busStop.latitude to busStop.longitude)
            _mapMarker.postValue(mapUtil.busStopsToMarkers(listOf(busStop)) to true)

            val busArrivals = getBusBusArrivalsUseCase(busStop.code)
            val listItems: MutableList<RecyclerViewListItem> =
                busArrivals
                    .map {
                        if (it.arrivals is Arrivals.Arriving) {
                            BusArrivalCompactItem(
                                busStop.code,
                                it,
                                ::onStarToggle,
                                {}
                            )
                        } else {
                            BusArrivalErrorItem(
                                busStop.code,
                                it,
                                ::onStarToggle
                            )
                        }
                    }
                    .toMutableList()

            _mapMarker.postValue(mapUtil.busArrivalsToMarkers(busArrivals) to false)

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
            _listItems.postValue(listItems to false)
            _loading.postValue(Loading.Hide)

            // start arrivals loop
            arrivalsLoopJob?.cancelAndJoin()
            arrivalsLoopJob = startArrivalsLoop(busStop,
                REFRESH_DELAY
            )
        }

    private var arrivalsLoopJob: Job? = null

    private fun startDelayedArrivalsLoop(busStop: BusStop) {
        arrivalsLoopJob = startArrivalsLoop(busStop,
            REFRESH_DELAY
        )
    }

    private fun startArrivalsLoop(busStop: BusStop, initialDelay: Long = 0) =
        viewModelScope.launch(dispatcherProvider.computation + arrivalsLoopErrorHandler) {
            lastBusStop = busStop
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
                                    ::onStarToggle,
                                    {})
                            } else {
                                BusArrivalErrorItem(
                                    busStop.code,
                                    it,
                                    ::onStarToggle
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
                    _listItems.postValue(listItems to true)
                    _mapMarker.postValue(mapUtil.busStopsToMarkers(listOf(busStop)) to true)
                    _mapMarker.postValue(mapUtil.busArrivalsToMarkers(busArrivals) to false)
                }
                delay(REFRESH_DELAY)
            }
        }

    private fun onStarToggle(busStopCode: String, busArrival: BusArrival) {
        toggleBusStopStar(busStopCode, busArrival)
    }

    private fun toggleBusStopStar(busStopCode: String, busArrival: BusArrival) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            toggleBusStopStarUseCase(busStopCode, busArrival.serviceNumber)
        }

    private fun restoreLastState(appState: AppState) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _loading.postValue(Loading.Hide)
            _clearMap.postValue(Unit)
            _mapCenter.postValue(
                appState.circleCenterLat to appState.circleCenterLon
            )
            _mapCircle.postValue(
                appState.circleCenterLat to appState.circleCenterLon to appState.circleRadius
            )
            _listItems.postValue(appState.listItems to false)
            _mapMarker.postValue(appState.markerOptionsList to false)
        }

    fun onBackPressed() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        lastBusStop = null
        arrivalsLoopJob?.cancelAndJoin()
        lastAppState?.let { restoreLastState(it) } ?: fetchDataInit()
        _showBack.postValue(false)
    }
}

data class AppState(
    val listItems: MutableList<RecyclerViewListItem>,
    val markerOptionsList: List<MarkerOptions>,
    val circleCenterLat: Double,
    val circleCenterLon: Double,
    val circleRadius: Double
)

data class Alert(
    val msg: String = "Something went wrong.",
    @DrawableRes val iconResId: Int = R.drawable.ic_error_128
)