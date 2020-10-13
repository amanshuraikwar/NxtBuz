package io.github.amanshuraikwar.nxtbuz.common.model

sealed class LocationOutput {
    object PermissionsNotGranted : LocationOutput()
    object CouldNotGetLocation : LocationOutput()
    data class Success(
        val latitude: Double,
        val longitude: Double
    ) : LocationOutput()
}