package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class GetHomeBusStopUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(): BusStop? {
        return busStopRepository.getBusStop(
            userRepository.getHomeBusStopCode()
                ?: return null
        )
    }
}