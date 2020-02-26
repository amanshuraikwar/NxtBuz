package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.model.BusArrival
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class GetArrivalsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusArrival> {
        return userRepository.getBusArrivals(busStopCode)
    }
}