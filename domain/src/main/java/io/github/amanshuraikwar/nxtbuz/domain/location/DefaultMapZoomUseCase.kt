package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.data.location.LocationRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class DefaultMapZoomUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    suspend operator fun invoke(): Float {
        // TODO: 24/1/21 update impl
        return 16f
    }
}