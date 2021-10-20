package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

class GetHomeBusStopUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository
) {
    operator fun invoke(
        completion: (IosResult<BusStop?>) -> Unit
    ) {
        completion from {
            busStopRepository.getBusStop(
                userRepository.getHomeBusStopCode()
                    ?: return@from null
            )
        }
    }
}