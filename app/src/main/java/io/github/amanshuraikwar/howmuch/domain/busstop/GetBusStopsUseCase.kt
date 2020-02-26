package io.github.amanshuraikwar.howmuch.domain.busstop

import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import javax.inject.Inject

class GetBusStopsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStop> {
        return userRepository.getCloseBusStops(latitude, longitude, limit)
    }
}