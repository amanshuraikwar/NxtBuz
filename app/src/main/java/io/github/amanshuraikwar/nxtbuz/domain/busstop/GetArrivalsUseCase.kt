package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class GetArrivalsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusArrival> {
        return userRepository.getBusArrivals(busStopCode)
    }
}