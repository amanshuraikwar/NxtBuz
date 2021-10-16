package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import javax.inject.Inject

class BusStopsQueryLimitUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(): Int {
        return busStopRepository.getBusStopQueryLimit()
    }

    suspend operator fun invoke(newLimit: Int) {
        busStopRepository.setBusStopQueryLimit(newLimit)
    }
}
