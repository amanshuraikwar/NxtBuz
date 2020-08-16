package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.busstop.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import javax.inject.Inject

class GetBusStopUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(busStopCode: String): BusStop {
        return busStopRepository.getBusStop(busStopCode)
    }
}