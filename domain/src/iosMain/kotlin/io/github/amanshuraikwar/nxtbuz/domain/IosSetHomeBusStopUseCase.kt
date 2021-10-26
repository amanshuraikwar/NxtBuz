package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.user.SetHomeBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class IosSetHomeBusStopUseCase(
    repo: UserRepository
) : SetHomeBusStopUseCase(
    repo = repo
) {
    operator fun invoke(
        busStopCode: String,
        completion: (IosResult<Unit>) -> Unit
    ) {
        completion from {
            invoke(busStopCode = busStopCode)
        }
    }
}