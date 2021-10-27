package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.locationdata.LocationEmitter
import javax.inject.Inject

class CleanupLocationUpdatesUseCase @Inject constructor(
    private val locationEmitter: LocationEmitter,
) {
    operator fun invoke() {
        return locationEmitter.cleanup()
    }
}