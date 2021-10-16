package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosResult
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository

class GetHomeBusStopUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository
) {
    operator fun invoke(
        completion: (IosResult<BusStop?>) -> Unit
    ) {
        returnIosResult(
            completion
        ) {
            busStopRepository.getBusStop(
                userRepository.getHomeBusStopCode()
                    ?: return@returnIosResult null
            )
        }
    }
}