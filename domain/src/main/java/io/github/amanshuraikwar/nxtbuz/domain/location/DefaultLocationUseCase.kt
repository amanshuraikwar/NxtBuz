package io.github.amanshuraikwar.nxtbuz.domain.location

import io.github.amanshuraikwar.nxtbuz.data.location.LocationRepository
import javax.inject.Inject

class DefaultLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Pair<Double, Double> {
        return locationRepository.getDefaultLocation()
    }
}