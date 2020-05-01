package io.github.amanshuraikwar.nxtbuz.ui.main.fragment

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
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.BusRouteViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busstop.BusStopArrivalsViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busstops.BusStopsViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class MainFragmentViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val toggleBusStopStar: ToggleBusStopStarUseCase,
    @Named("listItems") _listItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("onBackPressed") private val _onBackPressed: MutableLiveData<Unit>,
    private val busStopsViewModelDelegate: BusStopsViewModelDelegate,
    private val busStopArrivalsViewModelDelegate: BusStopArrivalsViewModelDelegate,
    private val mapViewModelDelegate: MapViewModelDelegate,
    private val busRouteViewModelDelegate: BusRouteViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel(),
    MapViewModelDelegate by mapViewModelDelegate {

    private val screenStateBackStack: Stack<ScreenState> = Stack()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
    }

    val listItems = _listItems.map { it }
    val loading = _loading.map { it }
    val onBackPressed = _onBackPressed.asEvent()

    private val _showBack = MutableLiveData<Boolean>()
    val showBack = _showBack

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
        pushNewScreenState(screenState)
        startScreenState(screenState)
    }

    fun onBusStopClicked(busStop: BusStop) {
        viewModelScope.launch(errorHandler) {
            val screenState = ScreenState.BusStopState(busStop)
            pushNewScreenState(screenState)
            startScreenState(screenState)
        }
    }

    private fun onStarToggle(busStopCode: String, busArrival: BusArrival) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            toggleBusStopStar(busStopCode, busArrival.serviceNumber)
        }
    }

    private fun onBusServiceClicked(busStop: BusStop, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            val screenState =
                ScreenState.BusRouteState(
                    busStop,
                    busServiceNumber
                )
            pushNewScreenState(screenState)
            startScreenState(screenState)
        }
    }

    private suspend fun pushNewScreenState(screenState: ScreenState) =
        withContext(dispatcherProvider.io) {
            if (screenStateBackStack.isNotEmpty()) {
                stopScreenState(screenStateBackStack.peek())
            }
            screenStateBackStack.push(screenState)
        }

    private suspend fun startScreenState(screenState: ScreenState) =
        withContext(dispatcherProvider.io) {

            when(screenState) {
                is ScreenState.BusStopsState -> {
                    busStopsViewModelDelegate.start(screenState, ::onBusStopClicked)
                }
                is ScreenState.BusStopState -> {
                    busStopArrivalsViewModelDelegate.start(
                        screenState,
                        ::onStarToggle,
                        ::onBusServiceClicked,
                        viewModelScope
                    )
                }
                is ScreenState.BusRouteState -> {
                    busRouteViewModelDelegate.start(screenState, viewModelScope, ::onBusStopClicked)
                }
            }

            if (screenStateBackStack.size > 1) {
                _showBack.postValue(true)
            } else {
                _showBack.postValue(false)
            }
        }

    private suspend fun stopScreenState(screenState: ScreenState) =
        withContext(dispatcherProvider.io) {
            when(screenState) {
                is ScreenState.BusStopsState -> {
                    busStopsViewModelDelegate.stop(screenState)
                }
                is ScreenState.BusStopState -> {
                    busStopArrivalsViewModelDelegate.stop(screenState)
                }
                is ScreenState.BusRouteState -> {
                    busRouteViewModelDelegate.stop(screenState)
                }
            }
        }

    fun onBackPressed() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        if (screenStateBackStack.size > 1) {
            val currentScreenState = screenStateBackStack.pop()
            stopScreenState(currentScreenState)
            startScreenState(screenStateBackStack.peek())
            if (screenStateBackStack.size == 1) {
                _showBack.postValue(false)
            } else {
                _showBack.postValue(true)
            }
        } else {
            Log.w(TAG, "onBackPressed: Called when back stack's size was less than 2.")
        }
    }

    companion object {
        private const val TAG = "OverviewViewModelNew"
    }
}