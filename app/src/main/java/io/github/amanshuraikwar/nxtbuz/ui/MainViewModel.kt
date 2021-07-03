package io.github.amanshuraikwar.nxtbuz.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.user.UserState
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.nxtbuz.settings.ui.delegate.AppThemeDelegate
import io.github.amanshuraikwar.nxtbuz.settings.ui.delegate.AppThemeDelegateImpl
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import io.github.amanshuraikwar.nxtbuz.ui.model.NavigationState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "MainViewModel"

class MainViewModel @Inject constructor(
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
    private val userStateUseCase: GetUserStateUseCase,
    private val busStopUseCase: GetBusStopUseCase,
    private val shouldShowMapUseCase: ShouldShowMapUseCase,
    appThemeDelegateImpl: AppThemeDelegateImpl,
    dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel(), AppThemeDelegate by appThemeDelegateImpl {

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = dispatcherProvider.computation + errorHandler

    private val _screenState =
        MutableStateFlow<MainScreenState>(MainScreenState.Fetching)
    val screenState: StateFlow<MainScreenState> = _screenState

    private val backStack = Stack<NavigationState>()
    private var showMap = false

    internal fun onInit() {
        viewModelScope.launch(coroutineContext) {
            refreshTheme()
            showMap = shouldShowMapUseCase()
            FirebaseCrashlytics.getInstance().setCustomKey("showMap", showMap)
            when (userStateUseCase()) {
                UserState.New -> {
                    _screenState.value = MainScreenState.Setup
                }
                UserState.SetupComplete -> {
                    synchronized(_screenState) {
                        when (val currentState = _screenState.value) {
                            MainScreenState.Fetching,
                            MainScreenState.Setup -> {
                                _screenState.value = MainScreenState.Success(
                                    showMap = showMap,
                                    navigationState = NavigationState.BusStops,
                                    showBackBtn = backStack.isNotEmpty()
                                )
                            }
                            is MainScreenState.Success -> {
                                if (showMap != currentState.showMap) {
                                    _screenState.value = MainScreenState.Success(
                                        showMap = showMap,
                                        navigationState = currentState.navigationState,
                                        showBackBtn = backStack.isNotEmpty()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        cleanupLocationUpdatesUseCase()
    }

    @Synchronized
    fun onBusStopClick(busStop: BusStop, pushBackStack: Boolean = true) {
        synchronized(_screenState) {
            val currentState = _screenState.value
            if (currentState is MainScreenState.Success) {
                val currentNavState = currentState.navigationState
                if (currentNavState is NavigationState.BusStopArrivals) {
                    if (currentNavState.busStop == busStop) {
                        return
                    }
                }
            }

            if (pushBackStack) {
                pushBackStack()
            }

            _screenState.value = MainScreenState.Success(
                showMap = showMap,
                navigationState = NavigationState.BusStopArrivals(busStop = busStop),
                showBackBtn = backStack.isNotEmpty()
            )
        }
    }

    @Synchronized
    fun onBusServiceClick(
        busStopCode: String,
        busServiceNumber: String,
        pushBackStack: Boolean = true
    ) {
        synchronized(_screenState) {
            val currentState = _screenState.value
            if (currentState is MainScreenState.Success) {
                val currentNavState = currentState.navigationState
                if (currentNavState is NavigationState.BusRoute) {
                    if (currentNavState.busStopCode == busStopCode &&
                        currentNavState.busServiceNumber == busServiceNumber
                    ) {
                        return
                    }
                }
            }

            if (pushBackStack) {
                pushBackStack()
            }

            _screenState.value = MainScreenState.Success(
                showMap = showMap,
                navigationState = NavigationState.BusRoute(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber,
                ),
                showBackBtn = backStack.isNotEmpty()
            )
        }
    }

    private fun pushBackStack() {
        val currentState = screenState.value
        if (currentState is MainScreenState.Success) {
            backStack.push(currentState.navigationState)
        }
    }

    @Synchronized
    fun onBackPressed(): Boolean {
        synchronized(_screenState) {
            return if (backStack.isNotEmpty()) {
                _screenState.value = MainScreenState.Success(
                    showMap = showMap,
                    navigationState = backStack.pop(),
                    showBackBtn = backStack.isNotEmpty()
                )
                true
            } else {
                false
            }
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
        synchronized(_screenState) {
            pushBackStack()
            _screenState.value = MainScreenState.Success(
                showMap = showMap,
                navigationState = NavigationState.Search,
                showBackBtn = backStack.size > 1
            )
        }
    }
}