package io.github.amanshuraikwar.nxtbuz.domain.busstop

import io.github.amanshuraikwar.nxtbuz.data.model.BusStop
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class GetBusStopsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, limit: Int): List<BusStop> {
        return userRepository.getCloseBusStops(lat, lon, limit)
    }

    suspend operator fun invoke(query: String, limit: Int): List<BusStop> {
        return userRepository.searchBusStops(query, limit)
    }
}