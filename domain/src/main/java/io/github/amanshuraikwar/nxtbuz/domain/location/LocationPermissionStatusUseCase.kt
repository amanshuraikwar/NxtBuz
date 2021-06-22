package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.common.model.location.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.data.location.LocationEmitter
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationPermissionStatusUseCase @Inject constructor(
    private val locationEmitter: LocationEmitter,
) {
    operator fun invoke(): StateFlow<PermissionStatus> {
        return locationEmitter.locationPermissionStatus
    }

    suspend fun refresh() {
        locationEmitter.refreshLocationPermissionStatus()
    }
}


