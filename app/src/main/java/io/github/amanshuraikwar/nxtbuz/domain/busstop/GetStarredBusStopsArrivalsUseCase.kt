package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.user.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class GetStarredBusStopsArrivalsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<StarredBusArrival> {
        return userRepository.getStarredBusStopsArrivals()
    }
}