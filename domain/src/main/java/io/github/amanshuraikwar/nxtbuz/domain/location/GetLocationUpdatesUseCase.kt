package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.common.model.location.Location
import io.github.amanshuraikwar.nxtbuz.data.location.LocationEmitter
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetLocationUpdatesUseCase @Inject constructor(
    private val locationEmitter: LocationEmitter,
) {

    suspend operator fun invoke(): StateFlow<Location> {
        return locationEmitter.getLocation()
    }
}


