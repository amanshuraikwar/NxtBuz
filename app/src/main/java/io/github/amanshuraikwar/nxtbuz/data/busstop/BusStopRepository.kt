package io.github.amanshuraikwar.nxtbuz.data.busstop

import androidx.annotation.IntRange
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busapi.SgBusApi
import io.github.amanshuraikwar.nxtbuz.data.busapi.model.BusStopItem
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.Bus
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopsDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BusStopRepository @Inject constructor(
    private val busStopDao: BusStopDao,
    private val operatingBusDao: OperatingBusDao,
    private val starredBusStopsDao: StarredBusStopsDao,
    private val busApi: SgBusApi,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @ExperimentalCoroutinesApi
    fun setup(): Flow<Double> =
        flow {
            setupActual(this)
        }

    private suspend fun setupActual(flowCollector: FlowCollector<Double>) =
        withContext(dispatcherProvider.io) {

            flowCollector.emit(0.0)

            busStopDao.deleteAll()

            var skip = 0

            val busStopItemList = mutableListOf<BusStopItem>()
            while (true) {
                val fetchedBusStops = busApi.getBusStops(skip).busStops
                if (fetchedBusStops.isEmpty()) break
                busStopItemList.addAll(fetchedBusStops)
                skip += 500
            }

            flowCollector.emit(0.5)

            busStopDao.insertAll(
                busStopItemList.map {
                    BusStopEntity(
                        it.code,
                        it.roadName,
                        it.description,
                        it.latitude,
                        it.longitude
                    )
                }
            )

            flowCollector.emit(1.0)
        }

    suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStop> = withContext(dispatcherProvider.io) {

        busStopDao
            .findCloseLimit(latitude, longitude, limit)
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
                            .map {
                                Bus(it.busServiceNumber)
                            }
                    )
                }
            }.awaitAll()
    }

    suspend fun getBusStopQueryLimit(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.busStopsQueryLimit
    }

    suspend fun setBusStopQueryLimit(
        @IntRange(from = 1, to = Long.MAX_VALUE) newLimit: Int
    ) = withContext(dispatcherProvider.io) {
        preferenceStorage.busStopsQueryLimit = newLimit
    }

    suspend fun getMaxDistanceOfClosesBusStop(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.maxDistanceOfClosestBusStop
    }

    suspend fun setMaxDistanceOfClosesBusStop(
        @IntRange(from = 0, to = Long.MAX_VALUE) newMaxDistance: Int
    ) = withContext(dispatcherProvider.io) {
        preferenceStorage.maxDistanceOfClosestBusStop = newMaxDistance
    }

    suspend fun searchBusStops(query: String, limit: Int): List<BusStop> =
        withContext(dispatcherProvider.io) {
            busStopDao
                .searchLikeDescription(query, limit)
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
        }

    suspend fun toggleBusStopStar(busStopCode: String, busServiceNumber: String): Unit =
        withContext(dispatcherProvider.io) {
            val isAlreadyStarred =
                starredBusStopsDao.findByBusStopCodeAndBusServiceNumber(
                    busStopCode,
                    busServiceNumber
                ).isNotEmpty()
            if (isAlreadyStarred) {
                starredBusStopsDao.deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                )
            } else {
                starredBusStopsDao.insertAll(
                    listOf(
                        StarredBusStopEntity(busStopCode, busServiceNumber)
                    )
                )
            }
        }

    suspend fun toggleBusStopStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean
    ): Unit = withContext(dispatcherProvider.io) {

        val isAlreadyStarred =
            starredBusStopsDao.findByBusStopCodeAndBusServiceNumber(
                busStopCode,
                busServiceNumber
            ).isNotEmpty()

        if (toggleTo != isAlreadyStarred) {
            if (toggleTo) {
                starredBusStopsDao.insertAll(
                    listOf(
                        StarredBusStopEntity(busStopCode, busServiceNumber)
                    )
                )
            } else {
                starredBusStopsDao.deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode, busServiceNumber
                )
            }
        }
    }

    suspend fun getBusStop(busStopCode: String): BusStop = withContext(dispatcherProvider.io) {
        busStopDao
            .findByCode(busStopCode)
            .let { list ->
                if (list.isEmpty()) {
                    throw Exception("No bus stop found for code $busStopCode")
                } else {
                    list[0]
                }
            }
            .let { busStopEntity ->
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
    }
}