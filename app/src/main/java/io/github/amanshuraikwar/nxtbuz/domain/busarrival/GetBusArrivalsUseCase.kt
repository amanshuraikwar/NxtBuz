package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.data.busarrival.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import javax.inject.Inject

class GetBusArrivalsUseCase @Inject constructor(
    private val busArrivalRepository: BusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusArrival> {
        return busArrivalRepository.getBusArrivals(busStopCode)
    }
}