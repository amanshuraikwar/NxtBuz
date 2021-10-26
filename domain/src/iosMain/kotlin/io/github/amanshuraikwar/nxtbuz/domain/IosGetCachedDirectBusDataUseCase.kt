package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetCachedDirectBusDataUseCase
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository

class IosGetCachedDirectBusDataUseCase(
    busStopRepository: BusStopRepository
) : GetCachedDirectBusDataUseCase(
    busStopRepository = busStopRepository
) {
    operator fun invoke(
        callback: (IosResult<Int>) -> Unit
    ) {
        callback from {
            invoke()
        }
    }
}