package io.github.amanshuraikwar.nxtbuz.map.ui.recenter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionStatusUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.PushMapEventUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RecenterViewModel"

class RecenterViewModel @Inject constructor(
    private val getLastKnownLocationUseCase: io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val locationPermissionStatusUseCase: io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionStatusUseCase,
    private val getLocationUpdatesUseCase: io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase,
    dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {
    private val _recenterButtonState =
        MutableStateFlow<RecenterButtonState>(RecenterButtonState.LocationAvailable)
    val recenterButtonState = _recenterButtonState.asStateFlow()

    private var lastLocationOutput: LocationOutput.Success? = null
    private var job: Job? = null

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.map

    @Synchronized
    fun init() {
        job?.cancel()
        job = null
        job = viewModelScope.launch(coroutineContext) {
            updateState(getLastKnownLocationUseCase())
            getLocationUpdatesUseCase().collect { locationOutput ->
                updateState(locationOutput)
            }
        }
    }

    private fun updateState(locationOutput: LocationOutput) {
        when (locationOutput) {
            is LocationOutput.Error,
            is LocationOutput.PermissionsNotGranted,
            is LocationOutput.SettingsNotEnabled -> {
                FirebaseCrashlytics.getInstance().setCustomKey("locationAvailable", false)
                _recenterButtonState.value = RecenterButtonState.LocationNotAvailable
            }
            is LocationOutput.Success -> {
                FirebaseCrashlytics.getInstance().setCustomKey("locationAvailable", true)
                lastLocationOutput = locationOutput
                _recenterButtonState.value = RecenterButtonState.LocationAvailable
            }
        }
    }

    fun recenterClick() {
        viewModelScope.launch(coroutineContext) {
            locationPermissionStatusUseCase.refresh()
            lastLocationOutput?.let { locationOutput ->
                pushMapEventUseCase(
                    MapEvent.MoveCenter(
                        locationOutput.lat,
                        locationOutput.lng
                    )
                )
            }
        }
    }
}