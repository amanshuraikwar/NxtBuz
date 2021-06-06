package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
    private val busStopUseCase: GetBusStopUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    private val coroutineContext = dispatcherProvider.computation

    private val _screenState =
        MutableStateFlow<MainScreenState>(MainScreenState.BusStops)
    val screenState: StateFlow<MainScreenState> = _screenState

    private val backStack = Stack<MainScreenState>()

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
        _screenState.value = MainScreenState.BusStopArrivals(busStop = busStop)
    }

    @Synchronized
    fun onBusServiceClick(busStopCode: String, busServiceNumber: String) {
        pushBackStack()
        _screenState.value = MainScreenState.BusRoute(
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber
        )
    }

    private fun pushBackStack() {
        backStack.push(screenState.value)
    }

    @Synchronized
    fun onBackPressed(): Boolean {
        return if (backStack.isNotEmpty()) {
            _screenState.value = backStack.pop()
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
        _screenState.value = MainScreenState.Search
    }
}