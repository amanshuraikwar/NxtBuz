package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.data.location.LocationRepository
import io.github.amanshuraikwar.nxtbuz.domain.location.model.LocationOutput
import io.github.amanshuraikwar.nxtbuz.util.PermissionUtil
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
    private val permissionUtil: PermissionUtil
) {

    suspend operator fun invoke(): LocationOutput {

        if (!permissionUtil.hasLocationPermission()) {
            return LocationOutput.PermissionsNotGranted
        }

        val location =
            locationRepository.getLastKnownLocation() ?: return LocationOutput.CouldNotGetLocation

        return LocationOutput.Success(
            location.latitude,
            location.longitude
        )
    }
}


