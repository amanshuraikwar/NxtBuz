package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.busstop.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import javax.inject.Inject

class GetBusStopsUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, limit: Int): List<BusStop> {
        return busStopRepository.getCloseBusStops(
            lat = lat,
            lng = lon,
            max = limit,
            maxDistanceMetres = 500
        )
    }

    suspend operator fun invoke(query: String, limit: Int): List<BusStop> {
        return busStopRepository.searchBusStops(query, limit)
    }
}