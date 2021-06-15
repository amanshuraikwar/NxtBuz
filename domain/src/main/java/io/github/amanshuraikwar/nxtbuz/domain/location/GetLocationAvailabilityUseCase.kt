package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.data.location.LocationEmitter
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetLocationAvailabilityUseCase @Inject constructor(
    private val locationEmitter: LocationEmitter,
) {
    suspend operator fun invoke(): StateFlow<Boolean> {
        return locationEmitter.getLocationAvailability()
    }
}


