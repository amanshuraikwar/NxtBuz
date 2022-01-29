package io.github.amanshuraikwar.nxtbuz.domain.arrivals

import io.github.amanshuraikwar.nxtbuz.domain.arrivals.model.BusStopArrivalResult
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

open class GetBusArrivalsUseCase constructor(
    private val busArrivalRepository: BusArrivalRepository,
    private val starredBusArrivalRepository: StarredBusArrivalRepository
) {
    suspend operator fun invoke(busStopCode: String): List<BusStopArrivalResult> {
        return coroutineScope {
            val starredBusServiceNumberSetDeferred = async(start = CoroutineStart.DEFAULT) {
                starredBusArrivalRepository.getStarredBusServices(atBusStopCode = busStopCode)
                    .map {
                        it.busServiceNumber
                    }
                    .toSet()
            }

            val busArrivals = busArrivalRepository.getBusArrivals(busStopCode)
            val starredBusServiceNumberSet = starredBusServiceNumberSetDeferred.await()

            busArrivals
                .map { busStopArrival ->
                    BusStopArrivalResult(
                        busStopArrival = busStopArrival,
                        isStarred = starredBusServiceNumberSet
                            .contains(busStopArrival.busServiceNumber)
                    )
                }
        }
    }

    suspend operator fun invoke(
        busStopCode: String,
        busServiceNumber: String
    ): BusStopArrivalResult {
        return coroutineScope {
            val isStarred = async(start = CoroutineStart.DEFAULT) {
                starredBusArrivalRepository.isBusServiceStarred(busStopCode, busServiceNumber)
            }

            BusStopArrivalResult(
                busStopArrival = busArrivalRepository.getBusArrivals(busStopCode, busServiceNumber),
                isStarred = isStarred.await()
            )
        }
    }
}