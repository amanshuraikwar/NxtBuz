package io.github.amanshuraikwar.nxtbuz.map.ui.recenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionStatusUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecenterViewModel @Inject constructor(
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val locationPermissionStatusUseCase: LocationPermissionStatusUseCase,
    private val getLocationUpdatesUseCase: GetLocationUpdatesUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {
    private val _recenterButtonState =
        MutableStateFlow<RecenterButtonState>(RecenterButtonState.LocationAvailable)
    val recenterButtonState = _recenterButtonState.asStateFlow()

    private var lastLocationOutput: LocationOutput.Success? = null
    private var job: Job? = null

    @Synchronized
    fun init() {
        job?.cancel()
        job = null
        job = viewModelScope.launch(dispatcherProvider.computation) {
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
                _recenterButtonState.value = RecenterButtonState.LocationNotAvailable
            }
            is LocationOutput.Success -> {
                lastLocationOutput = locationOutput
                _recenterButtonState.value = RecenterButtonState.LocationAvailable
            }
        }
    }

    fun recenterClick() {
        viewModelScope.launch(dispatcherProvider.computation) {
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