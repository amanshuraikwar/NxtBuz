package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

open class GetCachedDirectBusDataUseCase(
    private val busStopRepository: BusStopRepository,
) {
    suspend operator fun invoke(): Int {
        return busStopRepository.getCachedDirectBusesStopPermutationsCount()
    }
}

