package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsScreenState
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.StopsFilter
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationSettingsState
import io.github.amanshuraikwar.nxtbuz.common.model.location.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.LaunchBusStopsPage
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetStarredBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationSettingStateUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionDeniedPermanentlyUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionStatusUseCase
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetLaunchBusStopPageUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "BusStopsViewModel"

class BusStopsViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val getDefaultLocationUseCase: DefaultLocationUseCase,
    private val getLocationSettingStateUseCase: GetLocationSettingStateUseCase,
    private val locationPermissionStatusUseCase: LocationPermissionStatusUseCase,
    private val getStarredBusStopsUseCase: GetStarredBusStopsUseCase,
    private val permissionUtil: PermissionUtil,
    private val navigationUtil: NavigationUtil,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val locationPermissionDeniedPermanentlyUseCase: LocationPermissionDeniedPermanentlyUseCase,
    private val toggleBusStopStarUseCase: ToggleBusStopStarUseCase,
    private val getLaunchBusStopPageUseCase: GetLaunchBusStopPageUseCase,
    private val getTrainStopsUseCase: GetTrainStopsUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed()
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private var listItems = SnapshotStateList<BusStopsItemData>()

    private val _screenState = MutableStateFlow<BusStopsScreenState>(BusStopsScreenState.Fetching)
    val screenState: StateFlow<BusStopsScreenState> = _screenState

    private var listenStarUpdatesJob: Job? = null
    private val busStopListLock = Mutex()

    init {
        listenToggleStarUpdate()
    }

    fun init() {
        viewModelScope.launch(coroutineContext) {
            when (getLaunchBusStopPageUseCase()) {
                LaunchBusStopsPage.NearBy -> {
                    fetchNearbyBusStops(waitForSettings = false)
                }
                LaunchBusStopsPage.Starred -> {
                    fetchStarredBusStops()
                }
            }
        }
    }

    fun fetchNearbyBusStops(
        waitForSettings: Boolean = false
    ) {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
        FirebaseCrashlytics.getInstance().setCustomKey("waitForSettings", waitForSettings)

        viewModelScope.launch(coroutineContext) {
            withContext(dispatcherProvider.main) {
                _screenState.emit(
                    BusStopsScreenState.NearbyBusStops.Fetching(
                        filter = StopsFilter.BUS_STOPS_ONLY
                    )
                )
            }

            if (waitForSettings) {
                var count = 0
                while (
                    getLocationSettingStateUseCase() !is LocationSettingsState.SettingsEnabled &&
                    count < 10
                ) {
                    count++
                    delay(300)
                }
            }

            var location: LocationOutput.Success? = null

            when (val locationOutput = getLastKnownLocationUseCase()) {
                is LocationOutput.Error -> {
                    _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                        title = "Something went wrong while getting your location :(",
                        primaryButtonText = "RETRY",
                        onPrimaryButtonClick = {
                            fetchNearbyBusStops()
                        },
                        secondaryButtonText = "USE DEFAULT LOCATION",
                        onSecondaryButtonClick = {
                            fetchNearDefaultLocationBusStops()
                        },
                        filter = StopsFilter.BUS_STOPS_ONLY
                    )
                    return@launch
                }
                is LocationOutput.PermissionsNotGranted -> {
                    when (locationOutput.permissionStatus) {
                        PermissionStatus.DENIED -> {
                            _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                                title = "We need location permission to get nearby bus stops :)",
                                primaryButtonText = "GIVE PERMISSION",
                                onPrimaryButtonClick = {
                                    askPermissions()
                                },
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = {
                                    fetchNearDefaultLocationBusStops()
                                },
                                filter = StopsFilter.BUS_STOPS_ONLY
                            )
                        }
                        PermissionStatus.GRANTED -> {
                            // do nothing
                        }
                        PermissionStatus.DENIED_PERMANENTLY -> {
                            _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                                title = "We need location permission to get nearby bus stops :)",
                                primaryButtonText = "GO TO SETTINGS",
                                onPrimaryButtonClick = {
                                    goToAppSettings()
                                },
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = {
                                    fetchNearDefaultLocationBusStops()
                                },
                                filter = StopsFilter.BUS_STOPS_ONLY
                            )
                        }
                    }
                    return@launch
                }
                is LocationOutput.Success -> {
                    locationPermissionDeniedPermanentlyUseCase(false)
                    location = locationOutput
                }
                is LocationOutput.SettingsNotEnabled -> {
                    locationPermissionDeniedPermanentlyUseCase(false)
                    _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                        title = "Location is not turned on :(",
                        primaryButtonText = if (locationOutput.settingsState?.exception != null) {
                            "ENABLE LOCATION"
                        } else {
                            "RETRY"
                        },
                        onPrimaryButtonClick = {
                            val ex = locationOutput.settingsState?.exception
                            if (ex != null) {
                                askForSettingsChange(ex)
                            } else {
                                fetchNearbyBusStops(
                                    waitForSettings = true
                                )
                            }
                        },
                        secondaryButtonText = "USE DEFAULT LOCATION",
                        onSecondaryButtonClick = {
                            fetchNearDefaultLocationBusStops()
                        },
                        filter = StopsFilter.BUS_STOPS_ONLY
                    )
                    return@launch
                }
            }

            val busStopListItems = getListItems(
                getBusStopsUseCase(
                    lat = location.lat,
                    lon = location.lng,
                    limit = busStopsQueryLimitUseCase()
                ),
                "Bus Stops Nearby"
            )

            listItems = busStopListItems

            withContext(dispatcherProvider.main) {
                _screenState.emit(
                    BusStopsScreenState.NearbyBusStops.Success(
                        filter = StopsFilter.BUS_STOPS_ONLY,
                        listItems = busStopListItems
                    )
                )
            }
        }
    }

    private fun goToAppSettings() {
        viewModelScope.launch {
            navigationUtil.goToAppSettings()
            fetchNearbyBusStops()
        }
    }

    private fun getListItems(
        busStopList: List<BusStop>,
        headerTitle: String,
    ): SnapshotStateList<BusStopsItemData> {
        val listItems = SnapshotStateList<BusStopsItemData>()

        if (busStopList.isNotEmpty()) {
            listItems.add(
                BusStopsItemData.Header(
                    id = "bus-stops-screen-header",
                    title = headerTitle
                )
            )
        }

        listItems.addAll(
            busStopList.map { busStop ->
                busStop.toItem()
            }
        )

        return listItems
    }

    private fun askForSettingsChange(exception: ResolvableApiException) {
        viewModelScope.launch(coroutineContext) {
            val result = permissionUtil.askForSettingsChange(exception)
            Log.d(TAG, "askForSettingsChange: $result")
            if (result) {
                fetchNearbyBusStops(
                    waitForSettings = true
                )
            }

        }
    }

    private fun askPermissions() {
        viewModelScope.launch(coroutineContext) {
            when (permissionUtil.askPermission()) {
                PermissionStatus.GRANTED -> {
                    locationPermissionDeniedPermanentlyUseCase(false)
                }
                PermissionStatus.DENIED_PERMANENTLY -> {
                    FirebaseCrashlytics.getInstance().log("Permission denied permanently.")
                    locationPermissionDeniedPermanentlyUseCase(true)
                }
                PermissionStatus.DENIED -> {
                    FirebaseCrashlytics.getInstance().log("Permission denied.")
                }
            }
            locationPermissionStatusUseCase.refresh()
            fetchNearbyBusStops()
        }
    }

    private fun failed() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(BusStopsScreenState.Failed)
        }
    }

    fun onBusStopStarToggle(
        busStopCode: String,
        newStarState: Boolean
    ) {
        viewModelScope.launch(coroutineContext) {
            toggleBusStopStarUseCase(busStopCode = busStopCode, toggleTo = newStarState)
        }
    }

    private fun listenToggleStarUpdate() {
        listenStarUpdatesJob?.cancel()
        listenStarUpdatesJob = null
        listenStarUpdatesJob = viewModelScope.launch(coroutineContext) {
            toggleBusStopStarUseCase.updates()
                .collect { busStop ->
                    busStopListLock.withLock {
                        val listItemIndex =
                            listItems.indexOfFirst {
                                it is BusStopsItemData.BusStop
                                        && it.busStopCode == busStop.code
                            }

                        if (listItemIndex != -1) {
                            when (listItems[listItemIndex]) {
                                is BusStopsItemData.BusStop -> {
                                    if (_screenState.value is BusStopsScreenState.NearbyBusStops
                                        || _screenState.value
                                                is BusStopsScreenState.DefaultLocationBusStops
                                    ) {
                                        listItems[listItemIndex] = busStop.toItem()
                                    }

                                    if (_screenState.value is BusStopsScreenState.StarredBusStops) {
                                        if (busStop.isStarred) {
                                            listItems.add(busStop.toItem())
                                        } else {
                                            listItems.removeAt(listItemIndex)
                                        }

                                        if (listItems.size == 1) {
                                            fetchStarredBusStops()
                                        }
                                    }
                                }
                                else -> {
                                    // do nothing
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun BusStop.toItem(): BusStopsItemData.BusStop {
        return BusStopsItemData.BusStop(
            id = "bus-stops-screen-$code",
            busStopCode = code,
            busStopDescription = description,
            busStopInfo = "$roadName • $code",
            operatingBuses = operatingBusList
                .map { it.serviceNumber }
                .reduceRight { next, total ->
                    val first = when (total.length) {
                        2 -> "$total  "
                        3 -> "$total "
                        else -> total
                    }
                    val second = when (next.length) {
                        2 -> "$next  "
                        3 -> "$next "
                        else -> next
                    }
                    "$first  $second"
                },
            isStarred = isStarred
        )
    }

    fun fetchStarredBusStops() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(BusStopsScreenState.StarredBusStops.Fetching)
            val listItems = getListItems(
                getStarredBusStopsUseCase(),
                "Starred bus stops"
            )
            _screenState.emit(
                BusStopsScreenState.StarredBusStops.Success(listItems = listItems)
            )
        }
    }

    fun fetchNearDefaultLocationBusStops() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(BusStopsScreenState.DefaultLocationBusStops.Fetching)
            val (lat, lng) = getDefaultLocationUseCase()
            val listItems = getListItems(
                getBusStopsUseCase(
                    lat = lat,
                    lon = lng,
                    limit = busStopsQueryLimitUseCase()
                ),
                "Near Default Location"
            )
            _screenState.emit(
                BusStopsScreenState.DefaultLocationBusStops.Success(listItems = listItems)
            )
        }
    }

    fun onStopsFilterClick(filter: StopsFilter) {
        viewModelScope.launch(coroutineContext) {
            val currentScreenState = _screenState.value
            when (filter) {
                StopsFilter.BUS_STOPS_ONLY -> {
                    if (currentScreenState is BusStopsScreenState.NearbyBusStops) {
                        fetchNearbyBusStops(waitForSettings = false)
                    }
                }
                StopsFilter.TRAIN_STOPS_ONLY -> {
                    if (currentScreenState is BusStopsScreenState.NearbyBusStops) {
                        fetchNearbyTrainStops()
                    }
                }
            }
        }
    }

    private fun fetchNearbyTrainStops(
        waitForSettings: Boolean = false
    ) {
        viewModelScope.launch(coroutineContext) {
            withContext(dispatcherProvider.main) {
                _screenState.emit(
                    BusStopsScreenState.NearbyBusStops.Fetching(
                        filter = StopsFilter.TRAIN_STOPS_ONLY
                    )
                )
            }

            if (waitForSettings) {
                var count = 0
                while (
                    getLocationSettingStateUseCase() !is LocationSettingsState.SettingsEnabled &&
                    count < 10
                ) {
                    count++
                    delay(300)
                }
            }

            var location: LocationOutput.Success? = null

            when (val locationOutput = getLastKnownLocationUseCase()) {
                is LocationOutput.Error -> {
                    _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                        title = "Something went wrong while getting your location :(",
                        primaryButtonText = "RETRY",
                        onPrimaryButtonClick = {
                            fetchNearbyTrainStops()
                        },
                        secondaryButtonText = "USE DEFAULT LOCATION",
                        onSecondaryButtonClick = {
                            fetchNearDefaultLocationBusStops()
                        },
                        filter = StopsFilter.TRAIN_STOPS_ONLY
                    )
                    return@launch
                }
                is LocationOutput.PermissionsNotGranted -> {
                    when (locationOutput.permissionStatus) {
                        PermissionStatus.DENIED -> {
                            _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                                title = "We need location permission to get nearby bus stops :)",
                                primaryButtonText = "GIVE PERMISSION",
                                onPrimaryButtonClick = {
                                    askPermissions()
                                },
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = {
                                    fetchNearDefaultLocationBusStops()
                                },
                                filter = StopsFilter.TRAIN_STOPS_ONLY
                            )
                        }
                        PermissionStatus.GRANTED -> {
                            // do nothing
                        }
                        PermissionStatus.DENIED_PERMANENTLY -> {
                            _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                                title = "We need location permission to get nearby bus stops :)",
                                primaryButtonText = "GO TO SETTINGS",
                                onPrimaryButtonClick = {
                                    goToAppSettings()
                                },
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = {
                                    fetchNearDefaultLocationBusStops()
                                },
                                filter = StopsFilter.TRAIN_STOPS_ONLY
                            )
                        }
                    }
                    return@launch
                }
                is LocationOutput.Success -> {
                    locationPermissionDeniedPermanentlyUseCase(false)
                    location = locationOutput
                }
                is LocationOutput.SettingsNotEnabled -> {
                    locationPermissionDeniedPermanentlyUseCase(false)
                    _screenState.value = BusStopsScreenState.NearbyBusStops.LocationError(
                        title = "Location is not turned on :(",
                        primaryButtonText = if (locationOutput.settingsState?.exception != null) {
                            "ENABLE LOCATION"
                        } else {
                            "RETRY"
                        },
                        onPrimaryButtonClick = {
                            val ex = locationOutput.settingsState?.exception
                            if (ex != null) {
                                askForSettingsChange(ex)
                            } else {
                                fetchNearbyBusStops(
                                    waitForSettings = true
                                )
                            }
                        },
                        secondaryButtonText = "USE DEFAULT LOCATION",
                        onSecondaryButtonClick = {
                            fetchNearDefaultLocationBusStops()
                        },
                        filter = StopsFilter.TRAIN_STOPS_ONLY
                    )
                    return@launch
                }
            }

            val trainStops = getTrainStopsUseCase(
//                lat = location.lat,
//                lon = location.lng,
                lat = 52.3676,
                lon = 4.9041,
                limit = busStopsQueryLimitUseCase()
            )

            val listItems = getTrainStopListItems(
                trainStops,
                "Nearby Train Stops"
            )

            withContext(dispatcherProvider.main) {
                _screenState.emit(
                    BusStopsScreenState.NearbyBusStops.Success(
                        filter = StopsFilter.TRAIN_STOPS_ONLY,
                        listItems = listItems
                    )
                )
            }
        }
    }

    private fun getTrainStopListItems(
        trainStopList: List<TrainStop>,
        headerTitle: String,
    ): SnapshotStateList<BusStopsItemData> {
        val listItems = SnapshotStateList<BusStopsItemData>()

        if (trainStopList.isNotEmpty()) {
            listItems.add(
                BusStopsItemData.Header(
                    id = "train-stops-screen-header",
                    title = headerTitle
                )
            )
        }

        listItems.addAll(
            trainStopList.map { trainStop ->
                BusStopsItemData.TrainStop(
                    id = "train-stops-screen-${trainStop.code}",
                    code = trainStop.code,
                    codeToDisplay = trainStop.codeToDisplay,
                    name = trainStop.name,
                    hasDepartureTimes = trainStop.hasDepartureTimes,
                    hasTravelAssistance = trainStop.hasTravelAssistance,
                    isStarred = trainStop.starred,
                    hasFacilities = trainStop.hasFacilities
                )
            }
        )

        return listItems
    }
}

