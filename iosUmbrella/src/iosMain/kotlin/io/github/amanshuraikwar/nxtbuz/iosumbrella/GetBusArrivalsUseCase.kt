package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.busarrivaldata.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class GetBusArrivalsUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusStopArrival> {
        return busArrivalRepository.getBusArrivals(busStopCode)
    }

    operator fun invoke(busStopCode: String, callback: (List<BusStopArrival>) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, _ ->
                // TODO-amanshuraikwar (22 Sep 2021 11:06:33 PM): send error state
            }
        ) {
            callback(invoke(busStopCode))
        }
    }

    suspend operator fun invoke(busStopCode: String, busServiceNumber: String): BusStopArrival {
        return busArrivalRepository.getBusArrivals(busStopCode, busServiceNumber)
    }

    operator fun invoke(
        busStopCode: String,
        busServiceNumber: String,
        callback: (BusStopArrival) -> Unit
    ) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
//            CoroutineExceptionHandler { th, _ ->
//                println(th)
//                // TODO-amanshuraikwar (22 Sep 2021 11:06:33 PM): send error state
//            }
        ) {
            callback(
                busArrivalRepository.getBusArrivals(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
            )
        }
    }
}