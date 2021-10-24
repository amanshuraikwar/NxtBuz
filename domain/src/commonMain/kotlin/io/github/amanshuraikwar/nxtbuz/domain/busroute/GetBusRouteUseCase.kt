package io.github.amanshuraikwar.nxtbuz.domain.busroute

import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRoute
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository

class GetBusRouteUseCase constructor(
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