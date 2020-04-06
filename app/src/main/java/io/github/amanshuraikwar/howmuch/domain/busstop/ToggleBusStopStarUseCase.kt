package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
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