package io.github.amanshuraikwar.nxtbuz.domain.busroute

import io.github.amanshuraikwar.nxtbuz.data.busroute.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRoute
import javax.inject.Inject

class GetBusRouteUseCase @Inject constructor(
    private val busRouteRepository: BusRouteRepository
) {
    suspend operator fun invoke(
        busServiceNumber: String,
        direction: Int? = null,
        busStopCode: String? = null
    ): BusRoute {
        return busRouteRepository.getBusRoute(busServiceNumber, direction, busStopCode)
    }
}