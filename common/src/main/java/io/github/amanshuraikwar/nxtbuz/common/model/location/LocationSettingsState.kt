package io.github.amanshuraikwar.nxtbuz.common.model.location

import com.google.android.gms.common.api.ResolvableApiException

sealed class LocationSettingsState {
    data class Error(val reason: String) : LocationSettingsState()
    object SettingsEnabled : LocationSettingsState()
    data class ResolvableError(
        val exception: ResolvableApiException,
    ) : LocationSettingsState()
}