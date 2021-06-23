package io.github.amanshuraikwar.nxtbuz.data.busstop

import androidx.annotation.IntRange
import io.github.amanshuraikwar.ltaapi.LtaApi
import io.github.amanshuraikwar.ltaapi.model.BusStopItemDto
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Bus
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.common.model.room.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.common.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusStopRepository @Inject constructor(
    private val busStopDao: BusStopDao,
    private val operatingBusDao: OperatingBusDao,
    private val busApi: LtaApi,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    fun setup(): Flow<Double> = flow {

        emit(0.0)

        busStopDao.deleteAll()

        // fetch bus stops from the api until the api returns an empty list
        var skip = 0
        val busStopItemList = mutableListOf<BusStopItemDto>()
        while (true) {
            val fetchedBusStops = busApi.getBusStops(skip).busStops
            if (fetchedBusStops.isEmpty()) break
            busStopItemList.addAll(fetchedBusStops)
            skip += 500
        }

        emit(0.5)

        // save all bus stops in local db
        busStopDao.insertAll(
            busStopItemList
                .distinctBy { it.code }
                .map {
                    BusStopEntity(
                        it.code,
                        it.roadName,
                        it.description,
                        it.latitude,
                        it.longitude
                    )
                }
        )

        emit(1.0)

    }.flowOn(dispatcherProvider.computation)

    @Suppress("unused")
    suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStop> = withContext(dispatcherProvider.io) {
        busStopDao
            .findClose(latitude, longitude, limit)
            .distinctBy { it.code }
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
            }
            .awaitAll()
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
                .distinctBy { it.code }
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

    suspend fun getCloseBusStops(
        lat: Double,
        lng: Double,
        max: Int,
        maxDistanceMetres: Int,
    ): List<BusStop> = withContext(dispatcherProvider.io) {
        busStopDao
            .findClose(
                latitude = lat,
                longitude = lng,
                limit = max,
            )
            .filter { busStopEntity ->
                MapUtil.measureDistanceMetres(
                    lat1 = lat,
                    lng1 = lng,
                    lat2 = busStopEntity.latitude,
                    lng2 = busStopEntity.longitude
                ) <= maxDistanceMetres
            }
            .distinctBy { it.code }
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
            }
            .awaitAll()
    }
}