package io.github.amanshuraikwar.nxtbuz.iosumbrella

import co.touchlab.stately.freeze
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusArrival
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals
//import io.github.amanshuraikwar.nxtbuz.domain.busarrival.GetBusArrivalsUseCase
//import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.commonkmm.loop.Loop
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosBusStopArrivalOutput
import io.github.amanshuraikwar.nxtbuz.iosumbrella.model.IosStarredBusArrivalOutput
import kotlinx.coroutines.*

class GetStarredBusArrivalsUseCase(
    private val getStarredBusServicesUseCase: GetStarredBusServicesUseCase,
    private val getBusArrivalsUseCase: GetBusArrivalsUseCase,
    private val getBusStopUseCase: GetBusStopUseCase,
    //private val showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
) {
    suspend fun getStarredBusArrivals(): List<StarredBusArrival> {
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
                            .map { busStopArrival ->
                                StarredBusArrival(
                                    busStopCode = busStopCode,
                                    busServiceNumber = busStopArrival.busServiceNumber,
                                    busStopDescription = busStopDescription,
                                    busArrivals = busStopArrival.busArrivals
                                )
                            }
//                            .let { starredBusArrivalList ->
//                                if (!showErrorStarredBusArrivalsUseCase()) {
//                                    starredBusArrivalList.filter { starredBusArrival ->
//                                        starredBusArrival.busArrivals is BusArrivals.Arriving
//                                    }
//                                } else {
//                                    starredBusArrivalList
//                                }
//                            }
                    }
                }
                .awaitAll()
                .flatten()
        }
    }

    fun getStarredBusArrivals(callback: (IosStarredBusArrivalOutput) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                callback(
                    IosStarredBusArrivalOutput.Error(
                        th.message ?: "Something went wrong!"
                    ).freeze()
                )
            }
        ) {
            callback(
                IosStarredBusArrivalOutput.Success(
                    getStarredBusArrivals()
                )
            )
        }
    }
}