package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class SetHomeBusStopUseCase(
    private val repo: UserRepository
) {
    operator fun invoke(
        busStopCode: String,
        completion: (IosResult<Unit>) -> Unit
    ) {
        completion.returnIosResult {
            repo.setHomeBusStopCode(busStopCode = busStopCode)
        }
    }
}