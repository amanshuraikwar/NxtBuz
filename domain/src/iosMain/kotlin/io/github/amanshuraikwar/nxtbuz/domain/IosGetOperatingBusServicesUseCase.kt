package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetOperatingBusServicesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository

class IosGetOperatingBusServicesUseCase constructor(
    busArrivalRepository: BusArrivalRepository,
) : GetOperatingBusServicesUseCase(
    busArrivalRepository = busArrivalRepository
) {
    operator fun invoke(
        busStopCode: String,
        callback: (IosResult<List<Bus>>) -> Unit
    ) {
        callback from {
            invoke(busStopCode)
        }
    }
}