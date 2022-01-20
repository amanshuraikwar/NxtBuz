package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

open class GetStarredBusArrivalsUseCase(
    private val getStarredBusServicesUseCase: GetStarredBusServicesUseCase,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
) {
    suspend operator fun invoke(): List<StarredBusArrival> {
        return coroutineScope {
            val busStopCodeBusServiceNumberSetMap = LinkedHashMap<String, MutableSet<String>>()

            getStarredBusServicesUseCase().forEach { starredBusService ->
                busStopCodeBusServiceNumberSetMap[starredBusService.busStopCode]
                    .let {
                        it ?: mutableSetOf<String>().apply {
                            busStopCodeBusServiceNumberSetMap[starredBusService.busStopCode] = this
                        }
                    }
                    .add(starredBusService.busServiceNumber)
            }

            // fetch bus stop arrivals per bus stop in parallel
            busStopCodeBusServiceNumberSetMap
                .map { (busStopCode, starredBusServiceSet) ->
                    async {
                        val busStop = getBusStopUseCase(busStopCode) ?: return@async null

                        getBusArrivalsUseCase(busStopCode)
                            .filter {
                                it.busStopArrival.busServiceNumber in starredBusServiceSet
                            }
                            .map { busStopArrivalResult ->
                                StarredBusArrival(
                                    busStopCode = busStopCode,
                                    busServiceNumber =
                                    busStopArrivalResult.busStopArrival.busServiceNumber,
                                    busStop = busStop,
                                    busArrivals =
                                    busStopArrivalResult.busStopArrival.busArrivals
                                )
                            }
                            .let { starredBusArrivalList ->
                                if (!showErrorStarredBusArrivalsUseCase()) {
                                    starredBusArrivalList.filter { starredBusArrival ->
                                        starredBusArrival.busArrivals is BusArrivals.Arriving
                                    }
                                } else {
                                    starredBusArrivalList
                                }
                            }
                    }
                }
                .awaitAll()
                .filterNotNull()
                .flatten()
                .reversed()
        }
    }
}