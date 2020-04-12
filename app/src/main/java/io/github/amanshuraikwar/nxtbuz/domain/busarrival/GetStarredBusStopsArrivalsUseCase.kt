package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.data.busarrival.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import javax.inject.Inject

class GetStarredBusStopsArrivalsUseCase @Inject constructor(
    private val busArrivalRepository: BusArrivalRepository
) {
    suspend operator fun invoke(): List<StarredBusArrival> {
        return busArrivalRepository.getStarredBusStopsArrivals()
    }
}