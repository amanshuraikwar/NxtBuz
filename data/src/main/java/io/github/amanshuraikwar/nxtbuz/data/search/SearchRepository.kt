package io.github.amanshuraikwar.nxtbuz.data.search

import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Bus
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.common.model.BusService
import io.github.amanshuraikwar.nxtbuz.common.model.SearchResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val busStopDao: BusStopDao,
    private val busRouteDao: BusRouteDao,
    private val operatingBusDao: OperatingBusDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {

    suspend fun search(query: String, limit: Int): SearchResult =
        withContext(dispatcherProvider.computation) {

            val busStopList =
                busStopDao.searchLikeDescription(query, limit)
                    .map { busStopEntity ->
                        async(dispatcherProvider.pool8) {
                            BusStop(
                                busStopEntity.code,
                                busStopEntity.roadName,
                                busStopEntity.description,
                                busStopEntity.latitude,
                                busStopEntity.longitude,
                                operatingBusDao
                                    .findByBusStopCode(busStopEntity.code)
                                    .map { Bus(it.busServiceNumber) }
                            )
                        }
                    }.awaitAll()

            val busServiceList =
                busRouteDao.searchLikeBusServiceNumber(query, limit)
                    .map { busRouteEntity ->
                        async {

                            val busRouteEntityList = busRouteDao
                                .findByBusServiceNumber(busRouteEntity.busServiceNumber)
                                .filter { it.direction == busRouteEntity.direction }
                                .sortedBy { it.stopSequence }

                            if (busRouteEntityList.isEmpty()) {
                                throw Exception("Bus route entity list is empty for bus service number ${busRouteEntity.busServiceNumber}.")
                            }

                            val originBusStopDescription =
                                busStopDao
                                    .findByCode(busRouteEntityList[0].busStopCode)
                                    .firstOrNull()
                                    ?.description
                                    ?: throw Exception(
                                        "No bus stop found for bus stop code " +
                                                busRouteEntityList[0].busStopCode
                                    )

                            val destinationBusStopDescription =
                                busStopDao
                                    .findByCode(busRouteEntityList.last().busStopCode)
                                    .firstOrNull()
                                    ?.description
                                    ?: throw Exception(
                                        "No bus stop found for bus stop code " +
                                                busRouteEntityList[0].busStopCode
                                    )

                            BusService(
                                busServiceNumber = busRouteEntity.busServiceNumber,
                                originBusStopDescription = originBusStopDescription,
                                destinationBusStopDescription = destinationBusStopDescription,
                                numberOfBusStops = busRouteEntityList.size,
                                distance = busRouteEntityList.last().distance,
                            )
                        }
                    }
                    .awaitAll()

            SearchResult(
                busStopList,
                busServiceList
            )
        }
}