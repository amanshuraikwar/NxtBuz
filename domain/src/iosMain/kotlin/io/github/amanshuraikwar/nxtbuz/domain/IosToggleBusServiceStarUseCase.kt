package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusServiceStarUseCase
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

class IosToggleBusServiceStarUseCase constructor(
    repo: StarredBusArrivalRepository
) : ToggleBusServiceStarUseCase(
    repo = repo
) {
    operator fun invoke(
        busStopCode: String,
        busServiceNumber: String,
        completion: (IosResult<Unit>) -> Unit
    ) {
        completion from {
            invoke(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber
            )
        }
    }

    operator fun invoke(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean,
        completion: (IosResult<Unit>) -> Unit
    ) {
        completion from {
            invoke(
                busStopCode = busStopCode,
                busServiceNumber = busServiceNumber,
                toggleTo = toggleTo
            )
        }
    }
}