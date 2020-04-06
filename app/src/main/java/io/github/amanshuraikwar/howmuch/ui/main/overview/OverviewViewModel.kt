package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.MarkerOptions
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.BusArrival
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.data.user.StarredBusArrival
import io.github.amanshuraikwar.howmuch.domain.busstop.*
import io.github.amanshuraikwar.howmuch.ui.list.*
import io.github.amanshuraikwar.howmuch.util.MapUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "OverviewViewModel"

private const val REFRESH_DELAY = 10000L

class OverviewViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getBusStopsLimitUseCase: GetBusStopsLimitUseCase,
    private val getDefaultLocationUseCase: GetDefaultLocationUseCase,
    private val getMaxDistanceOfClosesBusStopUseCase: GetMaxDistanceOfClosesBusStopUseCase,
    private val getBusArrivalsUseCase: GetArrivalsUseCase,
    private val toggleBusStopStarUseCase: ToggleBusStopStarUseCase,
    private val getStarredBusStopsArrivalsUseCase: GetStarredBusStopsArrivalsUseCase,
    @Named("onBackPressed") private val _onBackPressed: MutableLiveData<Unit>,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val mapUtil: MapUtil
) : ViewModel() {

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

    private val starredBusErrorHandler = CoroutineExceptionHandler { _, th ->
        // TODO: 6/4/20
    }

    private val _listItems = MutableLiveData<Pair<MutableList<RecyclerViewListItem>, Boolean>>()
    val listItems = _listItems.map { it }

    private val _busStopActivity = MutableLiveData<BusStop>()
    val busStopActivity = _busStopActivity.asEvent()

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
        startStarredBus()
    }

    private fun startStarredBus() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            while (true) {
                _starredListItems.postValue(
                    getStarredBusStopsArrivalsUseCase()
                        .mapNotNull {
                            if (it is StarredBusArrival.Arriving)
                                StarredBusArrivalItem(it)
                            else
                                null
                        }
                        .toMutableList()
                )
                delay(REFRESH_DELAY)
            }
        }

    private fun fetchDefaultLocationInit() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _initMap.postValue(getDefaultLocationUseCase())
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
                getBusStopsUseCase(lat = lat, lon = lon, limit = getBusStopsLimitUseCase())

            if (mapUtil.measureDistanceMetres(
                    lat,
                    lon,
                    busStopList.first().latitude,
                    busStopList.first().longitude
                ) > getMaxDistanceOfClosesBusStopUseCase()
            ) {
                _error.postValue(Alert("You are too far away."))
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
                        limit = getBusStopsLimitUseCase()
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
                    val (lat, lon) = getDefaultLocationUseCase()
                    _mapCenter.postValue(lat to lon)
                    val busStopList = getBusStopsUseCase(
                        lat = lat,
                        lon = lon,
                        limit = getBusStopsLimitUseCase()
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
                        limit = getBusStopsLimitUseCase()
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
            //listItems.add(HeaderItem("Nearby Bus Stops"))
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

            val busArrivals = getBusArrivalsUseCase(busStop.code)
            val listItems: MutableList<RecyclerViewListItem> =
                busArrivals
                    .map {
                        BusArrivalCompactItem(
                            busStop.code,
                            it,
                            ::onStarToggle
                        )
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
            arrivalsLoopJob = startArrivalsLoop(busStop, REFRESH_DELAY)
        }

    private var arrivalsLoopJob: Job? = null

    private fun startArrivalsLoop(busStop: BusStop, initialDelay: Long = 0) =
        viewModelScope.launch(dispatcherProvider.computation + errorHandler) {
            delay(initialDelay)
            while (isActive) {
                val busArrivals = getBusArrivalsUseCase(busStop.code)
                //lastUpdatedOn = OffsetDateTime.now().format(TimeUtil.TIME_READABLE_FORMATTER)
                val listItems: MutableList<RecyclerViewListItem> =
                    busArrivals
                        .map {
                            BusArrivalCompactItem(
                                busStop.code,
                                it,
                                ::onStarToggle
                            )
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
                _listItems.postValue(listItems to true)
                _mapMarker.postValue(mapUtil.busStopsToMarkers(listOf(busStop)) to true)
                _mapMarker.postValue(mapUtil.busArrivalsToMarkers(busArrivals) to false)
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

sealed class Loading {
    data class Show(@DrawableRes val avd: Int, val txt: String) : Loading()
    object Hide : Loading()
}