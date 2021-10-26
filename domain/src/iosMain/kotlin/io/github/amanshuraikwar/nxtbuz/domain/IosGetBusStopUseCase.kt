package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

class IosGetBusStopUseCase constructor(
    busStopRepository: BusStopRepository
) : GetBusStopUseCase(
    busStopRepository = busStopRepository
) {
    operator fun invoke(
        busStopCode: String,
        callback: (IosResult<BusStop>) -> Unit
    ) {
        callback from {
            invoke(busStopCode = busStopCode)
        }
    }

    operator fun invoke(
        lat: Double,
        lng: Double,
        callback: (IosResult<BusStop?>) -> Unit
    ) {
        callback from {
            invoke(
                lat = lat,
                lng = lng
            )
        }
    }
}