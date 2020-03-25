package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.busstop.BusStopsOutput
import io.github.amanshuraikwar.howmuch.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.howmuch.ui.busstop.BusStopActivity
import io.github.amanshuraikwar.howmuch.ui.list.BusStopItem
import io.github.amanshuraikwar.howmuch.util.PermissionUtil
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow
import kotlin.math.sqrt

private const val TAG = "OverviewViewModel"

class OverviewViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val permissionUtil: PermissionUtil
) : ViewModel() {

    var colorControlNormalResId: Int = 0

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

    var mapReady = false

    private val _mapCenter = MutableLiveData<Pair<Double, Double>>()
    val mapCenter = _mapCenter.map { it }

    private val _mapMarker = MutableLiveData<Pair<Pair<Double, Double>, List<BusStop>>>()
    val mapMarker = _mapMarker.map { it }

    init {
        fetchData()
    }

    var lastLat: Double = 0.0
    var lastLon: Double = 0.0

    fun shouldFetch(lat: Double, lon: Double): Boolean {
        return sqrt((lastLat - lat).pow(2.0) + (lastLon - lon).pow(2.0)) > 0.004
    }

    fun fetchData(lat: Double, lon: Double) = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        if (!shouldFetch(lat, lon)) {
            return@launch
        }
        _loading.postValue(true)
        handleBusStopOutput(getBusStopsUseCase(lat = lat, lon =  lon, limit = 30))
        lastLat = lat
        lastLon = lon
    }

    private fun fetchData() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        _loading.postValue(true)
        handleBusStopOutput(getBusStopsUseCase(limit = 30))
    }

    private suspend fun handleBusStopOutput(busStopsOutput: BusStopsOutput) {
        when (busStopsOutput) {
            BusStopsOutput.PermissionsNotGranted -> {
                _error.postValue(Alert(msg = "Permissions not granted."))
            }
            BusStopsOutput.CouldNotGetLocation -> {
                _error.postValue(Alert(msg = "Location not enabled."))
            }
            is BusStopsOutput.Success -> {
                val listItems = mutableListOf<RecyclerViewListItem>()
                busStopsOutput.busStopList.forEach {
                    listItems.add(
                        BusStopItem(
                            it,
                            R.drawable.ic_round_directions_bus_72,
                            ::onBusStopClicked,
                            ::onGotoClicked
                        )
                    )
                }
                // wait for map to be ready
                while (!mapReady) {
                    delay(1000)
                }
                _mapCenter.postValue(busStopsOutput.latLon)
                _mapMarker.postValue(busStopsOutput.latLon to busStopsOutput.busStopList)
                _busStops.postValue(listItems)
                _loading.postValue(false)
            }
        }
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