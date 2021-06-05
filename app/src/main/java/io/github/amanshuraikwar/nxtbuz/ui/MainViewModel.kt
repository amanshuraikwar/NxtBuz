package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
) : ViewModel() {

    private val _screenState = MutableStateFlow<MainScreenState>(MainScreenState.BusStops)
    val screenState: StateFlow<MainScreenState> = _screenState

    private val backStack = Stack<MainScreenState>()

    override fun onCleared() {
        viewModelScope.launch {
            cleanupLocationUpdatesUseCase()
        }
    }

    @Synchronized
    fun onBusStopClick(busStop: BusStop) {
        backStack.push(screenState.value)
        _screenState.value = MainScreenState.BusStopArrivals(busStop)
    }

    @Synchronized
    fun onBusServiceClick(busStopCode: String, busServiceNumber: String) {
        backStack.push(screenState.value)
        _screenState.value = MainScreenState.BusRoute(
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber
        )
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
}