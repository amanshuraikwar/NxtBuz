package io.github.amanshuraikwar.nxtbuz.domain

import io.github.amanshuraikwar.nxtbuz.commonkmm.user.SetupState
import io.github.amanshuraikwar.nxtbuz.domain.model.IosResult
import io.github.amanshuraikwar.nxtbuz.domain.user.DoSetupUseCase
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class IosDoSetupUseCase(
    userRepository: UserRepository,
    busStopRepository: BusStopRepository,
    busRouteRepository: BusRouteRepository,
) : DoSetupUseCase(
    userRepository = userRepository,
    busStopRepository = busStopRepository,
    busRouteRepository = busRouteRepository
) {
    operator fun invoke(
        callback: (IosResult<SetupState>) -> Unit
    ) {
        callback fromFlow {
            invoke()
        }
    }
}