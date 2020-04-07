package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.busstop.BusStopRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class DefaultLocationUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {

    suspend operator fun invoke(): Pair<Double, Double> {
        delay(500)
        return busStopRepository.getDefaultLocation()
    }
}