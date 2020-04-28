package io.github.amanshuraikwar.nxtbuz.ui.main.overview.neww

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.model.LocationOutput
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.ScreenState
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.busstop.BusStopViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.busstops.BusStopsViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.map.MapViewModelDelegate
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class OverviewViewModelNew @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val toggleBusStopStar: ToggleBusStopStarUseCase,
    @Named("listItems") _listItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    private val busStopsViewModelDelegate: BusStopsViewModelDelegate,
    private val busStopViewModelDelegate: BusStopViewModelDelegate,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel(),
    MapViewModelDelegate by mapViewModelDelegate {

    private val screenStateBackStack: Stack<ScreenState> = Stack()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
    }

    val listItems = _listItems.map { it }
    val loading = _loading.map { it }

    init {
        init()
    }

    private fun init() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        _loading.postValue(
            Loading.Show(
                R.drawable.avd_anim_nearby_bus_stops_loading_128,
                "Finding bus stops nearby..."
            )
        )
        val (defaultLat, defaultLng) = defaultLocationUseCase()
        initMap(defaultLat, defaultLng)
        val locationOutput = getLocationUseCase()
        val (lat, lng) = when (locationOutput) {
            is LocationOutput.PermissionsNotGranted,
            is LocationOutput.CouldNotGetLocation -> {
                defaultLocationUseCase()
            }
            is LocationOutput.Success -> {
                locationOutput.latitude to locationOutput.longitude
            }
        }
        val screenState =
            ScreenState.BusStopsState(
                lat,
                lng
            )
        screenStateBackStack.push(screenState)
        busStopsViewModelDelegate.start(screenState, ::onBusStopClicked)
    }

    private fun onBusStopClicked(busStop: BusStop) {
        viewModelScope.launch(errorHandler) {
            val screenState = ScreenState.BusStopState(busStop)
            screenStateBackStack.push(screenState)
            busStopViewModelDelegate.start(
                screenState,
                ::onStarToggle
            )
        }
    }

    private fun onStarToggle(busStopCode: String, busArrival: BusArrival) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            toggleBusStopStar(busStopCode, busArrival.serviceNumber)
        }
    }

    companion object {
        private const val TAG = "OverviewViewModelNew"
    }
}