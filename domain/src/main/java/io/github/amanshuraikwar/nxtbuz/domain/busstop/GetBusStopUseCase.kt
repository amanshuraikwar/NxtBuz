package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import javax.inject.Inject

class GetBusStopUseCase @Inject constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(busStopCode: String): BusStop {
        return busStopRepository.getBusStop(busStopCode)
    }

    suspend operator fun invoke(lat: Double, lng: Double): BusStop? {
        return busStopRepository
            .getCloseBusStops(
                lat = lat,
                lng = lng,
                max = 1,
                maxDistanceMetres = 500
            )
            .takeIf { it.isNotEmpty() }
            ?.get(0)
    }
}