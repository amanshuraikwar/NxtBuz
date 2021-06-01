package io.github.amanshuraikwar.nxtbuz.domain.starred

import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival
import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.loop.Loop
import kotlinx.coroutines.*

class StarredBusArrivalsLoop(
    private val getStarredBusServicesUseCase: GetStarredBusServicesUseCase,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    coroutineScope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
) : Loop<List<StarredBusArrival>>(
    coroutineScope = coroutineScope,
    dispatcher = dispatcher
) {
    override suspend fun getData(): List<StarredBusArrival> {
        return coroutineScope {
            val busStopCodeBusServiceNumberSetMap = getStarredBusServicesUseCase()
                .groupBy { starredBusService -> starredBusService.busStopCode }
                .mapValues { (_, list) -> list.map { it.busServiceNumber }.toSet() }

            // fetch bus stop arrivals per bus stop in parallel
            busStopCodeBusServiceNumberSetMap
                .map { (busStopCode, starredBusServiceSet) ->
                    async {
                        val busStopDescription = getBusStopUseCase(busStopCode).description

                        getBusArrivalsUseCase(busStopCode)
                            .filter {
                                it.busServiceNumber in starredBusServiceSet
                            }
                            .map {  busStopArrival ->
                                StarredBusArrival(
                                    busStopCode = busStopCode,
                                    busServiceNumber = busStopArrival.busServiceNumber,
                                    busStopDescription = busStopDescription,
                                    busArrivals = busStopArrival.busArrivals
                                )
                            }
                    }
                }
                .awaitAll()
                .flatten()
        }
    }
}