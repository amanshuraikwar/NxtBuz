package io.github.amanshuraikwar.nxtbuz.data.search

//import io.github.amanshuraikwar.nxtbuz.commonkmm.*
//import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
//import kotlinx.coroutines.async
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.withContext
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class SearchRepository @Inject constructor(
//    private val localDataSource: LocalDataSource,
//    private val dispatcherProvider: CoroutinesDispatcherProvider,
//) {
//    suspend fun search(query: String, limit: Int): SearchResult =
//        withContext(dispatcherProvider.computation) {
//
//            val busStopList =
//                localDataSource.findBusStopsByDescription(query, limit)
//                    .map { busStopEntity ->
//                        async(dispatcherProvider.pool8) {
//                            BusStop(
//                                busStopEntity.code,
//                                busStopEntity.roadName,
//                                busStopEntity.description,
//                                busStopEntity.latitude,
//                                busStopEntity.longitude,
//                                localDataSource
//                                    .findOperatingBuses(
//                                        busStopCode = busStopEntity.code
//                                    )
//                                    .map { Bus(it.busServiceNumber) }
//                            )
//                        }
//                    }
//                    .awaitAll()
//
//            val busServiceList =
//                localDataSource.findBusRouteByBusServiceNumber(query, limit)
//                    .map { busRouteEntity ->
//                        async {
//
//                            val busRouteEntityList = localDataSource
//                                .findBusRoute(busServiceNumber = busRouteEntity.busServiceNumber)
//                                .filter { it.direction == busRouteEntity.direction }
//                                .sortedBy { it.stopSequence }
//
//                            if (busRouteEntityList.isEmpty()) {
//                                throw Exception(
//                                    "Bus route entity list is empty for bus service number " +
//                                            "${busRouteEntity.busServiceNumber}."
//                                )
//                            }
//
//                            val originBusStopDescription =
//                                localDataSource
//                                    .findBusStopByCode(busRouteEntityList[0].busStopCode)
//                                    ?.description
//                                    ?: throw Exception(
//                                        "No bus stop found for bus stop code " +
//                                                busRouteEntityList[0].busStopCode
//                                    )
//
//                            val destinationBusStopDescription =
//                                localDataSource
//                                    .findBusStopByCode(
//                                        busRouteEntityList.last().busStopCode
//                                    )
//                                    ?.description
//                                    ?: throw Exception(
//                                        "No bus stop found for bus stop code " +
//                                                busRouteEntityList[0].busStopCode
//                                    )
//
//                            BusService(
//                                busServiceNumber = busRouteEntity.busServiceNumber,
//                                originBusStopDescription = originBusStopDescription,
//                                destinationBusStopDescription = destinationBusStopDescription,
//                                numberOfBusStops = busRouteEntityList.size,
//                                distance = busRouteEntityList.last().distance,
//                            )
//                        }
//                    }
//                    .awaitAll()
//
//            SearchResult(
//                busStopList,
//                busServiceList
//            )
//        }
//}