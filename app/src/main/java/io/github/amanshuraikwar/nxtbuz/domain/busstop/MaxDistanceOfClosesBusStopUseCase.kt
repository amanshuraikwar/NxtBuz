package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.busstop.BusStopRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MaxDistanceOfClosesBusStopUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {

    suspend operator fun invoke(): Int {
        delay(500)
        return busStopRepository.getMaxDistanceOfClosesBusStop()
    }

    suspend operator fun invoke(newMaxDistance: Int) {
        return busStopRepository.setMaxDistanceOfClosesBusStop(newMaxDistance)
    }
}