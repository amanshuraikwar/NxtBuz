package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

class BusStopsQueryLimitUseCase constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(): Int {
        return busStopRepository.getBusStopQueryLimit()
    }

    suspend operator fun invoke(newLimit: Int) {
        busStopRepository.setBusStopQueryLimit(newLimit)
    }
}
