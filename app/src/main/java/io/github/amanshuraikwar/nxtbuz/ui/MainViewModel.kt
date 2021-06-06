package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import io.github.amanshuraikwar.nxtbuz.ui.model.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
    private val busStopUseCase: GetBusStopUseCase,
    private val shouldShowMapUseCase: ShouldShowMapUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    private val coroutineContext = dispatcherProvider.computation

    private val _screenState =
        MutableStateFlow<MainScreenState>(MainScreenState.Fetching)
    val screenState: StateFlow<MainScreenState> = _screenState

    private val backStack = Stack<NavigationState>()
    private var showMap = false

    internal fun onInit() {
        viewModelScope.launch(coroutineContext) {
            showMap = shouldShowMapUseCase()
            when (val currentState = _screenState.value) {
                MainScreenState.Fetching -> {
                    _screenState.value = MainScreenState.Success(
                        showMap = showMap,
                        navigationState = NavigationState.BusStops
                    )
                }
                is MainScreenState.Success -> {
                    if (showMap != currentState.showMap) {
                        _screenState.value = MainScreenState.Success(
                            showMap = showMap,
                            navigationState = currentState.navigationState
                        )
                    }
                }
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch(coroutineContext) {
            cleanupLocationUpdatesUseCase()
        }
    }

    @Synchronized
    fun onBusStopClick(busStop: BusStop, pushBackStack: Boolean = true) {
        if (pushBackStack) {
            pushBackStack()
        }
        _screenState.value = MainScreenState.Success(
            showMap = showMap,
            navigationState = NavigationState.BusStopArrivals(busStop = busStop)
        )
    }

    @Synchronized
    fun onBusServiceClick(busStopCode: String, busServiceNumber: String) {
        pushBackStack()
        _screenState.value = MainScreenState.Success(
            showMap = showMap,
            navigationState = NavigationState.BusRoute(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber
            )
        )
    }

    private fun pushBackStack() {
        val currentState = screenState.value
        if (currentState is MainScreenState.Success) {
            backStack.push(currentState.navigationState)
        }
    }

    @Synchronized
    fun onBackPressed(): Boolean {
        return if (backStack.isNotEmpty()) {
            _screenState.value = MainScreenState.Success(
                showMap = showMap,
                navigationState = backStack.pop()
            )
            true
        } else {
            false
        }
    }

    fun onMapClick(latLng: LatLng) {
        viewModelScope.launch(coroutineContext) {
            busStopUseCase(
                lat = latLng.latitude,
                lng = latLng.longitude
            )?.let { busStop ->
                onBusStopClick(busStop)
            }
        }
    }

    fun onSearchClick() {
        pushBackStack()
        _screenState.value = MainScreenState.Success(
            showMap = showMap,
            navigationState = NavigationState.Search
        )
    }
}