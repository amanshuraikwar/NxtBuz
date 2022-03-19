package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop

open class GetBusStopsUseCase constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, limit: Int): List<BusStop> {
        return busStopRepository.getCloseBusStops(
            latitude = lat,
            longitude = lon,
            limit = limit,
            metres = 500
        )
    }
}