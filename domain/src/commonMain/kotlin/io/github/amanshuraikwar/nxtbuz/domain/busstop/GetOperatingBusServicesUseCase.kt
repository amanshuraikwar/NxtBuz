package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository

open class GetOperatingBusServicesUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository,
) {
    suspend operator fun invoke(busStopCode: String): List<Bus> {
        return busArrivalRepository.getOperatingBusServices(busStopCode)
    }
}