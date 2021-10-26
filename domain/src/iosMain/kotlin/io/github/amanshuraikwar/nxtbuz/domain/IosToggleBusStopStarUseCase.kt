package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository

class IosToggleBusStopStarUseCase constructor(
    repo: StarredBusArrivalRepository
) : ToggleBusStopStarUseCase(
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