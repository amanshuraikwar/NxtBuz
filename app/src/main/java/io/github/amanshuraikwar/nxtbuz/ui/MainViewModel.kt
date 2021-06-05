package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import io.github.amanshuraikwar.nxtbuz.ui.model.copy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
) : ViewModel() {

    private val _screenState =
        MutableStateFlow<MainScreenState>(MainScreenState.BusStops(searchVisible = false))
    val screenState: StateFlow<MainScreenState> = _screenState

    private val backStack = Stack<MainScreenState>()

    override fun onCleared() {
        viewModelScope.launch {
            cleanupLocationUpdatesUseCase()
        }
    }

    @Synchronized
    fun onBusStopClick(busStop: BusStop) {
        backStack.push(
            when (val currentState = _screenState.value) {
                is MainScreenState.BusRoute -> {
                    currentState.copy(
                        searchVisible = false,
                    )
                }
                is MainScreenState.BusStopArrivals -> {
                    currentState.copy(
                        searchVisible = false,
                    )
                }
                is MainScreenState.BusStops -> {
                    MainScreenState.BusStops(
                        searchVisible = false,
                    )
                }
            }
        )
        _screenState.value = MainScreenState.BusStopArrivals(
            searchVisible = false,
            busStop = busStop
        )
    }

    @Synchronized
    fun onBusServiceClick(busStopCode: String, busServiceNumber: String) {
        backStack.push(
            when (val currentState = _screenState.value) {
                is MainScreenState.BusRoute -> {
                    currentState.copy(
                        searchVisible = false,
                    )
                }
                is MainScreenState.BusStopArrivals -> {
                    currentState.copy(
                        searchVisible = false,
                    )
                }
                is MainScreenState.BusStops -> {
                    MainScreenState.BusStops(
                        searchVisible = false,
                    )
                }
            }
        )
        _screenState.value = MainScreenState.BusRoute(
            searchVisible = false,
            busStopCode = busStopCode,
            busServiceNumber = busServiceNumber
        )
    }

    @Synchronized
    fun onBackPressed(): Boolean {
        val currentState = _screenState.value
        return if (currentState.searchVisible) {
            when (currentState) {
                is MainScreenState.BusRoute -> {
                    _screenState.value = currentState.copy(
                        searchVisible = false,
                    )
                }
                is MainScreenState.BusStopArrivals -> {
                    _screenState.value = currentState.copy(
                        searchVisible = false,
                    )
                }
                is MainScreenState.BusStops -> {
                    _screenState.value = MainScreenState.BusStops(
                        searchVisible = false,
                    )
                }
            }
            true
        } else {
            if (backStack.isNotEmpty()) {
                _screenState.value = backStack.pop()
                true
            } else {
                false
            }
        }
    }

    @Synchronized
    fun onSearchScreenVisible() {
        when (val currentState = _screenState.value) {
            is MainScreenState.BusRoute -> {
                _screenState.value = currentState.copy(
                    searchVisible = true,
                )
            }
            is MainScreenState.BusStopArrivals -> {
                _screenState.value = currentState.copy(
                    searchVisible = true,
                )
            }
            is MainScreenState.BusStops -> {
                _screenState.value = MainScreenState.BusStops(
                    searchVisible = true,
                )
            }
        }
    }
}