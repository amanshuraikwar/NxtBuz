package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.user.StarredBusArrival
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class GetStarredBusStopsArrivalsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<StarredBusArrival> {
        return userRepository.getStarredBusStopsArrivals()
    }
}