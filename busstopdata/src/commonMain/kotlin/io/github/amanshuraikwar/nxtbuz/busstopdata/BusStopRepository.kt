package io.github.amanshuraikwar.nxtbuz.busstopdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.MapUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBusesResult
import io.github.amanshuraikwar.nxtbuz.localdatasource.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.DirectBusEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusStopItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BusStopRepositoryImpl constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BusStopRepository {
    override fun setup(): Flow<Double> = flow {
        emit(0.0)

        localDataSource.deleteAllBusStops()

        // fetch bus stops from the api until the api returns an empty list
        var skip = 0
        val busStopItemList = mutableListOf<BusStopItemDto>()
        while (true) {
            val fetchedBusStops = remoteDataSource.getBusStops(skip)
            if (fetchedBusStops.isEmpty()) break
            busStopItemList.addAll(fetchedBusStops)
            skip += 500
        }

        emit(0.5)

        // save all bus stops in local db
        localDataSource.insertBusStops(
            busStopItemList
                .distinctBy { it.code }
                .map {
                    BusStopEntity(
                        code = it.code,
                        roadName = it.roadName,
                        description = it.description,
                        latitude = it.lat,
                        longitude = it.lng
                    )
                }
        )

        emit(1.0)
    }.flowOn(dispatcherProvider.computation)

    @Suppress("unused")
    override suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int
    ): List<BusStop> = withContext(dispatcherProvider.io) {
        localDataSource
            .findCloseBusStops(latitude, longitude, limit)
            .distinctBy { it.code }
            .map { busStopEntity ->
                async(dispatcherProvider.pool8) {
                    BusStop(
                        busStopEntity.code,
                        busStopEntity.roadName,
                        busStopEntity.description,
                        busStopEntity.latitude,
                        busStopEntity.longitude,
                        localDataSource.findOperatingBuses(busStopEntity.code)
                            .map {
                                Bus(it.busServiceNumber)
                            }
                    )
                }
            }
            .awaitAll()
    }

    override suspend fun getBusStopQueryLimit(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.busStopsQueryLimit
    }

    override suspend fun setBusStopQueryLimit(
        newLimit: Int
    ) = withContext(dispatcherProvider.io) {
        preferenceStorage.busStopsQueryLimit = newLimit.coerceIn(1..Int.MAX_VALUE)
    }

    override suspend fun getMaxDistanceOfClosesBusStop(): Int = withContext(dispatcherProvider.io) {
        preferenceStorage.maxDistanceOfClosestBusStop
    }

    override suspend fun setMaxDistanceOfClosesBusStop(
        newMaxDistance: Int
    ) = withContext(dispatcherProvider.io) {
        preferenceStorage.maxDistanceOfClosestBusStop = newMaxDistance.coerceIn(1..Int.MAX_VALUE)
    }

    override suspend fun searchBusStops(query: String, limit: Int): List<BusStop> =
        withContext(dispatcherProvider.io) {
            localDataSource.findBusStopsByDescription(query, limit)
                .distinctBy { it.code }
                .map { busStopEntity ->
                    async(dispatcherProvider.pool8) {
                        BusStop(
                            busStopEntity.code,
                            busStopEntity.roadName,
                            busStopEntity.description,
                            busStopEntity.latitude,
                            busStopEntity.longitude,
                            localDataSource
                                .findOperatingBuses(busStopEntity.code)
                                .map { Bus(it.busServiceNumber) }
                        )
                    }
                }.awaitAll()
        }

    override suspend fun getBusStop(busStopCode: String): BusStop =
        withContext(dispatcherProvider.io) {
            localDataSource
                .findBusStopByCode(busStopCode)
                .let { busStopEntity ->
                    busStopEntity ?: throw Exception("No bus stop found for code $busStopCode")
                }
                .let { busStopEntity ->
                    BusStop(
                        busStopEntity.code,
                        busStopEntity.roadName,
                        busStopEntity.description,
                        busStopEntity.latitude,
                        busStopEntity.longitude,
                        localDataSource
                            .findOperatingBuses(busStopEntity.code)
                            .map { Bus(it.busServiceNumber) }
                    )
                }
        }

    override suspend fun getDirectBuses(
        sourceBusStopCode: String,
        destinationBusStopCode: String
    ): DirectBusesResult {
        return withContext(dispatcherProvider.io) {
            val directBusEntityList = localDataSource
                .findDirectBuses(
                    sourceBusStopCode = sourceBusStopCode,
                    destinationBusStopCode = destinationBusStopCode
                )

            when {
                directBusEntityList.isEmpty() -> {
                    DirectBusesResult.NotCachedYet
                }
                directBusEntityList.size == 1 && !directBusEntityList[0].hasDirectBus -> {
                    DirectBusesResult.NoDirectBuses
                }
                else -> {
                    val sourceBusStop = getBusStop(busStopCode = sourceBusStopCode)
                    val destinationBusStop = getBusStop(busStopCode = destinationBusStopCode)

                    DirectBusesResult.Success(
                        directBusList = directBusEntityList.map {
                            DirectBus(
                                sourceBusStopDescription = sourceBusStop.description,
                                sourceBusStopCode = sourceBusStopCode,
                                destinationBusStopDescription = destinationBusStop.description,
                                destinationBusStopCode = destinationBusStopCode,
                                busServiceNumber = it.busServiceNumber,
                                stops = it.stops,
                                distance = it.distance
                            )
                        }
                    )
                }
            }
        }
    }

    override suspend fun setDirectBuses(directBusList: List<DirectBus>) {
        withContext(dispatcherProvider.io) {
            localDataSource.insertDirectBuses(
                directBusList = directBusList.map {
                    DirectBusEntity(
                        sourceBusStopCode = it.sourceBusStopCode,
                        destinationBusStopCode = it.destinationBusStopCode,
                        hasDirectBus = true,
                        busServiceNumber = it.busServiceNumber,
                        stops = it.stops,
                        distance = it.distance
                    )
                }
            )
        }
    }

    override suspend fun setNoDirectBusesFor(
        sourceBusStopCode: String,
        destinationBusStopCode: String
    ) {
        withContext(dispatcherProvider.io) {
            localDataSource.insertDirectBuses(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = sourceBusStopCode,
                        destinationBusStopCode = destinationBusStopCode,
                        hasDirectBus = false,
                        busServiceNumber = "no-service",
                        stops = -1,
                        distance = -1.0
                    )
                )
            )
        }
    }

    override suspend fun getCloseBusStops(
        lat: Double,
        lng: Double,
        max: Int,
        maxDistanceMetres: Int,
    ): List<BusStop> = withContext(dispatcherProvider.io) {
        localDataSource.findCloseBusStops(
            lat = lat,
            lng = lng,
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
                        localDataSource
                            .findOperatingBuses(busStopEntity.code)
                            .map {
                                Bus(it.busServiceNumber)
                            }
                    )
                }
            }
            .awaitAll()
    }
}