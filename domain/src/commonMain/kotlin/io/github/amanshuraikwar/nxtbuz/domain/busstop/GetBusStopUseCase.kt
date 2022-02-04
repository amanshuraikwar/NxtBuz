package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

open class GetBusStopUseCase constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(busStopCode: String): BusStop? {
        return busStopRepository.getBusStop(busStopCode)
    }

    suspend operator fun invoke(lat: Double, lng: Double): BusStop? {
        return busStopRepository
            .getCloseBusStops(
                latitude = lat,
                longitude = lng,
                limit = 1,
                metres = 500
            )
            .takeIf { it.isNotEmpty() }
            ?.get(0)
    }
}