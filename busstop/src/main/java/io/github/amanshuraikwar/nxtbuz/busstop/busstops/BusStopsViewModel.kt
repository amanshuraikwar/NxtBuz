package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsScreenState
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationSettingsState
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

private const val TAG = "BusStopsViewModel"

class BusStopsViewModel @Inject constructor(
    private val getBusStopsUseCase: GetBusStopsUseCase,
    private val busStopsQueryLimitUseCase: BusStopsQueryLimitUseCase,
    private val getDefaultLocationUseCase: DefaultLocationUseCase,
    private val getLocationSettingStateUseCase: GetLocationSettingStateUseCase,
    private val locationPermissionStatusUseCase: LocationPermissionStatusUseCase,
    private val permissionUtil: PermissionUtil,
    private val navigationUtil: NavigationUtil,
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val locationPermissionDeniedPermanentlyUseCase: LocationPermissionDeniedPermanentlyUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        failed()
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private var listItems = SnapshotStateList<BusStopsItemData>()
    private val listItemsLock = Mutex()

    private val _screenState = MutableStateFlow<BusStopsScreenState>(BusStopsScreenState.Fetching)
    val screenState: StateFlow<BusStopsScreenState> = _screenState

    fun fetchBusStops(
        useDefaultLocation: Boolean = false,
        waitForSettings: Boolean = false
    ) {
        viewModelScope.launch(coroutineContext) {
            _screenState.value = BusStopsScreenState.Fetching

            if (useDefaultLocation) {

                val (lat, lng) = getDefaultLocationUseCase()
                emitBusStops(lat, lng, "Near Default Location")
                return@launch

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
                    _screenState.value = BusStopsScreenState.LocationError(
                        title = "Something went wrong while getting your location :(",
                        primaryButtonText = "RETRY",
                        onPrimaryButtonClick = {
                            fetchBusStops()
                        },
                        secondaryButtonText = "USE DEFAULT LOCATION",
                        onSecondaryButtonClick = {
                            fetchBusStops(
                                useDefaultLocation = true
                            )
                        }
                    )
                    return@launch
                }
                is LocationOutput.PermissionsNotGranted -> {
                    when (locationOutput.permissionStatus) {
                        PermissionStatus.DENIED -> {
                            _screenState.value = BusStopsScreenState.LocationError(
                                title = "We need location permission to get nearby bus stops :)",
                                primaryButtonText = "GIVE PERMISSION",
                                onPrimaryButtonClick = {
                                    askPermissions()
                                },
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = {
                                    fetchBusStops(
                                        useDefaultLocation = true
                                    )
                                }
                            )
                        }
                        PermissionStatus.GRANTED -> {
                            // do nothing
                        }
                        PermissionStatus.DENIED_PERMANENTLY -> {
                            _screenState.value = BusStopsScreenState.LocationError(
                                title = "We need location permission to get nearby bus stops :)",
                                primaryButtonText = "GO TO SETTINGS",
                                onPrimaryButtonClick = {
                                    goToAppSettings()
                                },
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = {
                                    fetchBusStops(
                                        useDefaultLocation = true
                                    )
                                }
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
                    _screenState.value = BusStopsScreenState.LocationError(
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
                                fetchBusStops(
                                    waitForSettings = true
                                )
                            }
                        },
                        secondaryButtonText = "USE DEFAULT LOCATION",
                        onSecondaryButtonClick = {
                            fetchBusStops(
                                useDefaultLocation = true
                            )
                        }
                    )
                    return@launch
                }
            }
            emitBusStops(
                location.lat,
                location.lng,
                "Bus Stops Nearby"
            )
        }
    }

    private fun goToAppSettings() {
        viewModelScope.launch {
            navigationUtil.goToAppSettings()
            fetchBusStops()
        }
    }

    private suspend fun emitBusStops(
        lat: Double,
        lng: Double,
        headerTitle: String,
    ) {
        val listItems = SnapshotStateList<BusStopsItemData>()

        val busStopList = getBusStopsUseCase(
            lat = lat,
            lon = lng,
            limit = busStopsQueryLimitUseCase()
        )

        if (busStopList.isNotEmpty()) {
            listItems.add(
                BusStopsItemData.Header(headerTitle)
            )
        }

        listItems.addAll(
            busStopList.map { busStop ->
                BusStopsItemData.BusStop(
                    busStopDescription = busStop.description,
                    busStopInfo = "${busStop.roadName} • ${busStop.code}",
                    operatingBuses = busStop.operatingBusList
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
                    busStop = busStop
                )
            }
        )

        listItemsLock.withLock {
            this@BusStopsViewModel.listItems = listItems
        }

        _screenState.emit(BusStopsScreenState.Success(this@BusStopsViewModel.listItems))
    }

    private fun askForSettingsChange(exception: ResolvableApiException) {
        viewModelScope.launch(coroutineContext) {
            val result = permissionUtil.askForSettingsChange(exception)
            Log.d(TAG, "askForSettingsChange: $result")
            if (result) {
                fetchBusStops(
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
                    locationPermissionDeniedPermanentlyUseCase(true)
                }
                PermissionStatus.DENIED -> {
                    // do nothing
                }
            }
            locationPermissionStatusUseCase.refresh()
            fetchBusStops()
        }
    }

    private fun failed() {
        viewModelScope.launch(coroutineContext) {
            _screenState.emit(BusStopsScreenState.Failed)
        }
    }
}

