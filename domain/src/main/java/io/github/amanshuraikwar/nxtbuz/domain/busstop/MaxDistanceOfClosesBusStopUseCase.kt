package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import javax.inject.Inject

class MaxDistanceOfClosesBusStopUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(): Int {
        return busStopRepository.getMaxDistanceOfClosesBusStop()
    }

    suspend operator fun invoke(newMaxDistance: Int) {
        return busStopRepository.setMaxDistanceOfClosesBusStop(newMaxDistance)
    }
}