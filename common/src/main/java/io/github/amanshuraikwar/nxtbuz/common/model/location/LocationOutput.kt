package io.github.amanshuraikwar.nxtbuz.common.model.location

import io.github.amanshuraikwar.nxtbuz.common.model.PermissionStatus

sealed class LocationOutput {
    data class PermissionsNotGranted(val permissionStatus: PermissionStatus) : LocationOutput()

    data class SettingsNotEnabled(
        val settingsState: LocationSettingsState.ResolvableError
    ) : LocationOutput()

    data class Error(val reason: String) : LocationOutput()

    data class Success(
        val lat: Double,
        val lng: Double
    ) : LocationOutput()
}