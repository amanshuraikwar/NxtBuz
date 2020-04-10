package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class ToggleBusStopStarUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String) {
        userRepository.toggleBusStopStar(
            busStopCode, busServiceNumber
        )
    }
}