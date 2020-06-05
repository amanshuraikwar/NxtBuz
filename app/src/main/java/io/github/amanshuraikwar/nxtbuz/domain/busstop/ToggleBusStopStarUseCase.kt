package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.busstop.BusStopRepository
import javax.inject.Inject

class ToggleBusStopStarUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String) {
        busStopRepository.toggleBusStopStar(
            busStopCode, busServiceNumber
        )
    }

    suspend operator fun invoke(busStopCode: String, busServiceNumber: String, toggleTo: Boolean) {
        busStopRepository.toggleBusStopStar(
            busStopCode, busServiceNumber, toggleTo
        )
    }
}