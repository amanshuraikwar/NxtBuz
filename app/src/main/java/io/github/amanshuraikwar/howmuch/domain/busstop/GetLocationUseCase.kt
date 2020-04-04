package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.util.PermissionUtil
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val permissionUtil: PermissionUtil
) {

    suspend operator fun invoke(): LocationOutput {

        if (!permissionUtil.hasLocationPermission()) {
            return LocationOutput.PermissionsNotGranted
        }

        val location =
            userRepository.getLastKnownLocation() ?: return LocationOutput.CouldNotGetLocation

        return LocationOutput.Success(
            location.latitude,
            location.longitude
        )
    }
}

sealed class LocationOutput {
    object PermissionsNotGranted : LocationOutput()
    object CouldNotGetLocation : LocationOutput()
    data class Success(
        val latitude: Double,
        val longitude: Double
    ) : LocationOutput()
}
