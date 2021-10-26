package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.user.GetHomeBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.model.HomeBusStopResult
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class IosGetHomeBusStopUseCase(
    userRepository: UserRepository,
    busStopRepository: BusStopRepository
) : GetHomeBusStopUseCase(
    userRepository = userRepository,
    busStopRepository = busStopRepository
) {
    operator fun invoke(
        completion: (IosResult<HomeBusStopResult>) -> Unit
    ) {
        completion from {
            invoke()
        }
    }
}