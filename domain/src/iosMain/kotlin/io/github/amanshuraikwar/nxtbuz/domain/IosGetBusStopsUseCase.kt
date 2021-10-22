package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

class IosGetBusStopsUseCase constructor(
    busStopRepository: BusStopRepository
) : GetBusStopsUseCase(busStopRepository = busStopRepository) {
    operator fun invoke(
        lat: Double,
        lon: Double,
        limit: Int,
        callback: (IosResult<List<BusStop>>) -> Unit
    ) {
        callback from {
            invoke(lat = lat, lon = lon, limit = limit)
        }
    }

    operator fun invoke(
        query: String,
        limit: Int,
        callback: (IosResult<List<BusStop>>) -> Unit
    ) {
        callback from {
            invoke(
                query = query,
                limit = limit
            )
        }
    }
}