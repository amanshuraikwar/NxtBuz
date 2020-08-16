package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.common.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busarrival.BusArrivalRepository
import javax.inject.Inject

class GetBusArrivalsUseCase @Inject constructor(
    private val busArrivalRepository: BusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusArrival> {
        return busArrivalRepository.getBusArrivals(busStopCode)
    }
    suspend operator fun invoke(busStopCode: String, busServiceNumber: String): BusArrival {
        return busArrivalRepository.getBusArrivals(busStopCode, busServiceNumber)
    }
}