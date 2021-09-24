package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.busarrivaldata.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.starreddata.StarredBusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosBusStopArrival
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosBusStopArrivalOutput
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class GetBusArrivalsUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository,
    private val starredBusArrivalRepository: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusStopArrival> {
        return busArrivalRepository.getBusArrivals(busStopCode)
    }

    operator fun invoke(busStopCode: String, callback: (IosBusStopArrivalOutput) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                callback(
                    IosBusStopArrivalOutput.Error(
                        th.message ?: "Something went wrong!"
                    ).freeze()
                )
            }
        ) {
            val starredBusServiceNumberSet =
                (starredBusArrivalRepository.getStarredBusServices() as List<StarredBusService>)
                    .filter {
                        it.busStopCode == busStopCode
                    }
                    .map {
                        it.busServiceNumber
                    }
                    .toSet()

            callback(
                IosBusStopArrivalOutput.Success (
                    invoke(busStopCode).map { busStopArrival ->
                        IosBusStopArrival(
                            busStopCode = busStopArrival.busStopCode,
                            busServiceNumber = busStopArrival.busServiceNumber,
                            operator = busStopArrival.operator,
                            direction = busStopArrival.direction,
                            stopSequence = busStopArrival.stopSequence,
                            distance = busStopArrival.distance,
                            busArrivals = busStopArrival.busArrivals,
                            starred = starredBusServiceNumberSet
                                .contains(busStopArrival.busServiceNumber)
                        )
                    }
                ).freeze()
            )
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