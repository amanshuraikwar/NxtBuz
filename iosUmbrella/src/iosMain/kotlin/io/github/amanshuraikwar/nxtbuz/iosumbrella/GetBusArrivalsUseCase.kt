package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.busarrivaldata.BusArrivalRepository
import kotlinx.coroutines.launch

class GetBusArrivalsUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusStopArrival> {
        return busArrivalRepository.getBusArrivals(busStopCode)
    }

    operator fun invoke(busStopCode: String, callback: (List<BusStopArrival>) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch {
            callback(invoke(busStopCode))
        }
    }

    suspend operator fun invoke(busStopCode: String, busServiceNumber: String): BusStopArrival {
        return busArrivalRepository.getBusArrivals(busStopCode, busServiceNumber)
    }
}