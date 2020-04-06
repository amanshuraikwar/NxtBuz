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
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.busstop.*
import io.github.amanshuraikwar.howmuch.ui.list.BusStopItem
import io.github.amanshuraikwar.howmuch.ui.list.HeaderItem
import io.github.amanshuraikwar.howmuch.util.MapUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val TAG = "OverviewViewModel"

class OverviewViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getBusStopsLimitUseCase: GetBusStopsLimitUseCase,
    private val getDefaultLocationUseCase: GetDefaultLocationUseCase,
    private val getMaxDistanceOfClosesBusStopUseCase: GetMaxDistanceOfClosesBusStopUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val mapUtil: MapUtil
) : ViewModel() {

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

    private val _busStops = MutableLiveData<MutableList<RecyclerViewListItem>>()
    val busStops = _busStops.map { it }

    private val _busStopActivity = MutableLiveData<BusStop>()
    val busStopActivity = _busStopActivity.asEvent()

    private val _goto = MutableLiveData<BusStop>()
    val goto = _goto.asEvent()

    private val _loading = MutableLiveData<Boolean>()
    val loading = _loading.asEvent()

    private val _locationStatus = MutableLiveData<Boolean>()
    val locationStatus = _locationStatus.asEvent()

    var mapReady = false

    private val _mapCenter = MutableLiveData<Pair<Double, Double>>()
    val mapCenter = _mapCenter.asEvent()

    private val _mapMarker = MutableLiveData<Pair<List<MarkerOptions>, Boolean>>()
    val mapMarker = _mapMarker.asEvent()

    private val _clearMap = MutableLiveData<Unit>()
    val clearMap = _clearMap.asEvent()

    private val _mapCircle = MutableLiveData<Pair<Pair<Double, Double>, Double>>()
    val mapCircle = _mapCircle.asEvent()

    private val _initMap = MutableLiveData<Pair<Double, Double>>()
    val initMap = _initMap.asEvent()

    private var lastLat: Double = 0.0
    private var lastLon: Double = 0.0

    init {
        fetchDefaultLocationInit()
        fetchDataInit()
    }

    private fun fetchDefaultLocationInit() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _initMap.postValue(getDefaultLocationUseCase())
        }

    fun fetchBusStopsForLatLon(lat: Double, lon: Double) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _loading.postValue(true)
            lastLat = lat
            lastLon = lon
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
            _busStops.postValue(getListItems(busStopList))
            _mapMarker.postValue(mapUtil.busStopsToMarkers(busStopList) to false)
            _loading.postValue(false)
        }

    fun searchBusStops(query: String) =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            if (query.isNotEmpty()) {
                _loading.postValue(true)
                val busStopList = getBusStopsUseCase(query, getBusStopsLimitUseCase())
                if (busStopList.isEmpty()) {
                    _error.postValue(Alert("No matching bus stops found."))
                    // TODO: 5/4/20
                } else {
                    _mapCenter.postValue(busStopList[0].latitude to busStopList[0].longitude)
                    _busStops.postValue(getListItems(busStopList))
                    _mapMarker.postValue(mapUtil.busStopsToMarkers(busStopList) to true)
                    _loading.postValue(false)
                }
            }
        }

    fun onRecenterClicked() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            when (val locationOutput = getLocationUseCase()) {
                is LocationOutput.PermissionsNotGranted,
                LocationOutput.CouldNotGetLocation -> {
                    _locationStatus.postValue(false)
                }
                is LocationOutput.Success -> {
                    _locationStatus.postValue(true)
                    lastLat = locationOutput.latitude
                    lastLon = locationOutput.longitude
                    _mapCenter.postValue(
                        locationOutput.latitude to locationOutput.longitude
                    )
                    _loading.postValue(true)
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
                    _busStops.postValue(getListItems(busStopList))
                    _mapMarker.postValue(mapUtil.busStopsToMarkers(busStopList) to false)
                    _loading.postValue(false)
                }
            }
        }

    private fun fetchDataInit() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            _loading.postValue(true)
            // wait for map to be ready
            while (!mapReady) {
                delay(1000)
            }
            when (val locationOutput = getLocationUseCase()) {
                is LocationOutput.PermissionsNotGranted,
                is LocationOutput.CouldNotGetLocation -> {
                    _locationStatus.postValue(false)
                    val (lat, lon) = getDefaultLocationUseCase()
                    lastLat = lat
                    lastLon = lon
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
                    _busStops.postValue(getListItems(busStopList))
                    _mapMarker.postValue(mapUtil.busStopsToMarkers(busStopList) to false)
                }
                is LocationOutput.Success -> {
                    _locationStatus.postValue(true)
                    lastLat = locationOutput.latitude
                    lastLon = locationOutput.longitude
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
                    _busStops.postValue(getListItems(busStopList))
                    _mapMarker.postValue(mapUtil.busStopsToMarkers(busStopList) to false)
                }
            }
            _loading.postValue(false)
        }

    private suspend fun getListItems(busStopList: List<BusStop>): MutableList<RecyclerViewListItem> =
        withContext(dispatcherProvider.computation) {
            val listItems = mutableListOf<RecyclerViewListItem>()
            //listItems.add(HeaderItem("Nearby Bus Stops"))
            busStopList.forEach {
                listItems.add(
                    BusStopItem(
                        it,
                        R.drawable.ic_bus_stop_128,
                        ::onBusStopClicked,
                        ::onGotoClicked
                    )
                )
            }
            listItems
        }

    private fun onBusStopClicked(busStop: BusStop) {
        _busStopActivity.postValue(busStop)
    }

    private fun onGotoClicked(busStop: BusStop) {
        _goto.postValue(busStop)
    }
}

data class Alert(
    val msg: String = "Something went wrong.",
    @DrawableRes val iconResId: Int = R.drawable.ic_round_error_48
)