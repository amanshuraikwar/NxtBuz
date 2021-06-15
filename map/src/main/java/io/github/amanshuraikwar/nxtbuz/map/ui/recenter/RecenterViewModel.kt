package io.github.amanshuraikwar.nxtbuz.map.ui.recenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.PushMapEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecenterViewModel @Inject constructor(
    private val getLastKnownLocationUseCase: GetLastKnownLocationUseCase,
    private val pushMapEventUseCase: PushMapEventUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {
    private val _recenterButtonState =
        MutableStateFlow<RecenterButtonState>(RecenterButtonState.LocationAvailable)
    val recenterButtonState = _recenterButtonState.asStateFlow()

    fun init() {
        viewModelScope.launch(dispatcherProvider.computation) {
            updateState(getLastKnownLocationUseCase())
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
                _recenterButtonState.value = RecenterButtonState.LocationAvailable
            }
        }
    }

    fun recenterClick() {
        viewModelScope.launch(dispatcherProvider.computation) {
            val locationOutput = getLastKnownLocationUseCase()
            if (locationOutput is LocationOutput.Success) {
                pushMapEventUseCase(
                    MapEvent.MoveCenter(
                        lat = locationOutput.lat,
                        lng = locationOutput.lng
                    )
                )
            }
            updateState(locationOutput)
        }
    }
}