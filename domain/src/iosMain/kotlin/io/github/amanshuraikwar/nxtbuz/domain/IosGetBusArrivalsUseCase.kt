package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.model.BusStopArrivalResult
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

class IosGetBusArrivalsUseCase constructor(
    busArrivalRepository: BusArrivalRepository,
    starredBusArrivalRepository: StarredBusArrivalRepository
) : GetBusArrivalsUseCase(
    busArrivalRepository = busArrivalRepository,
    starredBusArrivalRepository = starredBusArrivalRepository,
) {
    operator fun invoke(
        busStopCode: String,
        callback: (IosResult<List<BusStopArrivalResult>>) -> Unit
    ) {
        callback from {
            invoke(busStopCode = busStopCode)
        }
    }

    operator fun invoke(
        busStopCode: String,
        busServiceNumber: String,
        callback: (IosResult<BusStopArrivalResult>) -> Unit,
    ) {
        callback from {
            invoke(busStopCode = busStopCode, busServiceNumber = busServiceNumber)
        }
    }
}