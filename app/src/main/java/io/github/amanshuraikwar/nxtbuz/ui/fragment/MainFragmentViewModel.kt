package io.github.amanshuraikwar.nxtbuz.ui.fragment

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUseCase
import io.github.amanshuraikwar.nxtbuz.common.model.LocationOutput
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.common.model.screenstate.ScreenState
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.map.LocationViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.starred.ui.delegate.StarredArrivalsViewModelDelegate
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@FlowPreview
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class MainFragmentViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val toggleBusStopStar: ToggleBusStopStarUseCase,
    @Named("listItems") _listItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    @Named("loading") private val _loading: MutableLiveData<Loading>,
    @Named("onBackPressed") _onBackPressed: MutableLiveData<Unit>,
    @Named("starredListItems") _starredListItems: MutableLiveData<MutableList<RecyclerViewListItem>>,
    @Named("collapseBottomSheet") _collapseBottomSheet: MutableLiveData<Unit>,
    @Named("error") private val _error: MutableLiveData<Alert>,
    @Named("starToggleState") _starToggleStateFlow: MutableStateFlow<StarToggleState>,
    @Named("bottomSheetSlideOffset")
    private val bottomSheetSlideOffsetFlow: MutableStateFlow<Float>,
    //private val busStopsViewModel: BusStopsViewModel,
    private val busStopArrivalsViewModelDelegate: BusStopArrivalsViewModelDelegate,
    private val busRouteViewModelDelegate: io.github.amanshuraikwar.nxtbuz.busroute.BusRouteViewModelDelegateImpl,
    starredArrivalsViewModelDelegate: StarredArrivalsViewModelDelegate,
    private val locationViewModelDelegate: LocationViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel(),
    StarredArrivalsViewModelDelegate by starredArrivalsViewModelDelegate,
    io.github.amanshuraikwar.nxtbuz.busroute.BusRouteViewModelDelegate by busRouteViewModelDelegate {

    private val starToggleStateFlow: StateFlow<StarToggleState> = _starToggleStateFlow

    private val _starToggleState = MutableLiveData<StarToggleState>()
    val starToggleState = _starToggleState.asEvent()

    private val screenStateBackStack: Stack<ScreenState> = Stack()

    private val _locationStatus = MutableLiveData<Boolean>()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        _error.postValue(Alert())
    }

    val listItems = _listItems.map { it }
    val loading = _loading.map { it }
    val onBackPressed = _onBackPressed.asEvent()
    val starredListItems = _starredListItems.map { it }
    val collapseBottomSheet = _collapseBottomSheet.asEvent()
    val error: LiveData<Event<Alert>> = _error
        .map {
            Log.e(TAG, "onError: $it")
            it
        }
        .asEvent()
    val locationStatus = _locationStatus.map { it }

    private val _showBack = MutableLiveData<Boolean>()
    val showBack = _showBack

    private val _bottomSheetSlideOffset = MutableLiveData<Float>()
    val bottomSheetSlideOffset: LiveData<Float> = _bottomSheetSlideOffset

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel",
            TAG
        )
        init()
        starredArrivalsViewModelDelegate.start(viewModelScope, ::onBusServiceClicked)
    }

    private fun init() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        onRecenterClicked(true)
        launch {
            starToggleStateFlow.collect {
                _starToggleState.postValue(it)
            }
        }
        locationViewModelDelegate.init(viewModelScope)
        launch {
            bottomSheetSlideOffsetFlow.collect {
                _bottomSheetSlideOffset.postValue(it)
            }
        }
    }

    private fun fetchBusStopsForLatLon(lat: Double, lng: Double) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            if (screenStateBackStack.size >= 1) {
                val topScreenState = screenStateBackStack.peek()
                if (topScreenState is ScreenState.BusStopsState) {
                    screenStateBackStack.pop()
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

    private fun onStarToggle(busStopCode: String, busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            toggleBusStopStar(busStopCode, busServiceNumber)
        }
    }

    fun onBusServiceClicked(busStop: BusStop, busServiceNumber: String) {
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

    fun onBusServiceClicked(busServiceNumber: String) {
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            val screenState =
                ScreenState.BusRouteState(
                    busServiceNumber = busServiceNumber
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

    @InternalCoroutinesApi
    private suspend fun startScreenState(screenState: ScreenState) =
        withContext(dispatcherProvider.io) {

            when (screenState) {
                is ScreenState.BusStopsState -> {
//                    busStopsViewModel.start(
//                        screenState, ::onBusStopClicked, viewModelScope
//                    )
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
                    busRouteViewModelDelegate.start(
                        screenState,
                        viewModelScope,
                        ::onBusStopClicked,
                        ::onStarToggle
                    )
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
            when (screenState) {
                is ScreenState.BusStopsState -> {
                    //busStopsViewModel.stop(screenState)
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

    fun onRecenterClicked(
        useDefault: Boolean = false
    ) = viewModelScope.launch(dispatcherProvider.io + errorHandler) {

        if (screenStateBackStack.size >= 1) {
            val topScreenState = screenStateBackStack.peek()
            if (topScreenState is ScreenState.BusStopsState) {
                screenStateBackStack.pop()
            }
        }

        val locationOutput = getLocationUseCase()
        val (lat, lng) = when (locationOutput) {
            is LocationOutput.PermissionsNotGranted,
            is LocationOutput.CouldNotGetLocation -> {
                _locationStatus.postValue(false)
                if (useDefault) {
                    defaultLocationUseCase()
                } else {
                    return@launch
                }
            }
            is LocationOutput.Success -> {
                _locationStatus.postValue(true)
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

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        busStopArrivalsViewModelDelegate.clear()
    }

    fun bottomSheetCollapsed() = viewModelScope.launch(dispatcherProvider.computation) {
//        val currentScreenState = screenStateBackStack.peek()
//        if (currentScreenState is ScreenState.BusRouteState) {
//            busRouteViewModelDelegate.onBottomSheetCollapsed()
//        }
    }

    companion object {
        private const val TAG = "OverviewViewModelNew"
    }
}