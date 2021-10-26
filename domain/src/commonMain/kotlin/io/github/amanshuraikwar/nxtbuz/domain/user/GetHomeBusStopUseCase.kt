package io.github.amanshuraikwar.nxtbuz.domain.user

import io.github.amanshuraikwar.nxtbuz.domain.user.model.HomeBusStopResult
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository

open class GetHomeBusStopUseCase(
    private val userRepository: UserRepository,
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(): HomeBusStopResult {
        return HomeBusStopResult.Success(
            busStop = busStopRepository.getBusStop(
                userRepository.getHomeBusStopCode()
                    ?: return HomeBusStopResult.NotSet
            )
        )
    }
}