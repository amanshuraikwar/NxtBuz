package io.github.amanshuraikwar.nxtbuz.domain.location.model

sealed class LocationOutput {
    object PermissionsNotGranted : LocationOutput()
    object CouldNotGetLocation : LocationOutput()
    data class Success(
        val latitude: Double,
        val longitude: Double
    ) : LocationOutput()
}