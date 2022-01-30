package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

open class GetStarredBusStopsUseCase constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(): List<BusStop> {
        return busStopRepository.getStarredBusStops()
    }
}