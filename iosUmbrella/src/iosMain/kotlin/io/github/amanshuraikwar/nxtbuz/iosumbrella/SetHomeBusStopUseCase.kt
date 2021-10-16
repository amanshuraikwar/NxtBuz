package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository

class SetHomeBusStopUseCase(
    private val repo: UserRepository
) {
    operator fun invoke(
        busStopCode: String,
        completion: (IosResult<Unit>) -> Unit
    ) {
        returnIosResult(
            completion
        ) {
            repo.setHomeBusStopCode(busStopCode = busStopCode)
        }
    }
}