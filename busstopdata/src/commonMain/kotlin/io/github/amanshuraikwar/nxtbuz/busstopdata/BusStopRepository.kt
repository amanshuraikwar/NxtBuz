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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BusStopRepositoryImpl constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val nsApiRemoteDataSource: RemoteDataSource,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BusStopRepository {
    private val coroutineScope: CoroutineScope by lazy {
        // We use supervisor scope because we don't want
        // the child coroutines to cancel all the parent coroutines
        CoroutineScope(SupervisorJob() + dispatcherProvider.computation)
    }

    private val busStopUpdate = MutableSharedFlow<BusStop>()

    override fun setup(): Flow<Double> {
        return flow {
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
                            longitude = it.lng,
                            starred = false
                        )
                    }
            )

            emit(1.0)
        }.flowOn(dispatcherProvider.computation)
    }

    @Suppress("unused")
    override suspend fun getCloseBusStops(
        latitude: Double,
        longitude: Double,
        limit: Int,
        metres: Int?
    ): List<BusStop> {
        return withContext(dispatcherProvider.io) {
            localDataSource
                .findCloseBusStops(latitude, longitude, limit)
                .let {
                    if (metres != null) {
                        it.filter { busStopEntity ->
                            MapUtil.measureDistanceMetres(
                                lat1 = latitude,
                                lng1 = longitude,
                                lat2 = busStopEntity.latitude,
                                lng2 = busStopEntity.longitude
                            ) <= metres
                        }
                    } else {
                        it
                    }
                }
                .distinctBy { it.code }
                .map { busStopEntity ->
                    async(dispatcherProvider.pool8) {
                        BusStop(
                            code = busStopEntity.code,
                            roadName = busStopEntity.roadName,
                            description = busStopEntity.description,
                            latitude = busStopEntity.latitude,
                            longitude = busStopEntity.longitude,
                            operatingBusList = localDataSource.findOperatingBuses(busStopEntity.code)
                                .map {
                                    Bus(it.busServiceNumber)
                                },
                            isStarred = busStopEntity.starred
                        )
                    }
                }
                .awaitAll()
        }
    }

    override suspend fun getBusStopQueryLimit(): Int {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.busStopsQueryLimit
        }
    }

    override suspend fun setBusStopQueryLimit(newLimit: Int) {
        withContext(dispatcherProvider.io) {
            preferenceStorage.busStopsQueryLimit =
                newLimit.coerceAtLeast(1)
        }
    }

    override suspend fun getMaxDistanceOfClosesBusStop(): Int {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.maxDistanceOfClosestBusStop
        }
    }

    override suspend fun setMaxDistanceOfClosesBusStop(
        newMaxDistance: Int
    ) {
        return withContext(dispatcherProvider.io) {
            preferenceStorage.maxDistanceOfClosestBusStop =
                newMaxDistance.coerceAtLeast(1)
        }
    }

    override suspend fun getBusStop(busStopCode: String): BusStop? {
        return withContext(dispatcherProvider.io) {
            localDataSource
                .findBusStopByCode(busStopCode)
                ?.let { busStopEntity ->
                    BusStop(
                        code = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        description = busStopEntity.description,
                        latitude = busStopEntity.latitude,
                        longitude = busStopEntity.longitude,
                        operatingBusList = localDataSource
                            .findOperatingBuses(busStopEntity.code)
                            .map { Bus(it.busServiceNumber) },
                        isStarred = busStopEntity.starred
                    )
                }
        }
    }

    override suspend fun getStarredBusStops(): List<BusStop> {
        return withContext(dispatcherProvider.io) {
            localDataSource
                .findAllStarredBusStops()
                .map { busStopEntity ->
                    BusStop(
                        code = busStopEntity.code,
                        roadName = busStopEntity.roadName,
                        description = busStopEntity.description,
                        latitude = busStopEntity.latitude,
                        longitude = busStopEntity.longitude,
                        operatingBusList = localDataSource
                            .findOperatingBuses(busStopEntity.code)
                            .map { Bus(it.busServiceNumber) },
                        isStarred = busStopEntity.starred
                    )
                }
        }
    }

    override suspend fun toggleBusStopStar(busStopCode: String, toggleTo: Boolean?): Boolean {
        return withContext(dispatcherProvider.computation) {
            val busStopEntity =
                localDataSource.findBusStopByCode(busStopCode) ?: return@withContext false

            if (toggleTo != null) {
                if (toggleTo != busStopEntity.starred) {
                    if (toggleTo) {
                        localDataSource.updateBusStop(
                            busStop = busStopEntity.copy(starred = toggleTo)
                        )
                    } else {
                        localDataSource.updateBusStop(
                            busStop = busStopEntity.copy(starred = toggleTo)
                        )
                    }

                    coroutineScope.launch {
                        busStopUpdate.emit(
                            getBusStop(busStopCode = busStopCode) ?: return@launch
                        )
                    }
                }
            } else {
                localDataSource.updateBusStop(
                    busStop = busStopEntity.copy(starred = !busStopEntity.starred)
                )

                coroutineScope.launch {
                    busStopUpdate.emit(
                        getBusStop(busStopCode = busStopCode) ?: return@launch
                    )
                }
            }

            return@withContext true
        }
    }

    override suspend fun isBusStopStarred(busStopCode: String): Boolean {
        return withContext(dispatcherProvider.computation) {
            localDataSource.findBusStopByCode(busStopCode = busStopCode)?.starred ?: false
        }
    }

    override suspend fun busStopUpdates(): SharedFlow<BusStop> {
        return busStopUpdate
    }

    override suspend fun setDirectBuses(directBusList: List<DirectBus>) {
        withContext(dispatcherProvider.io) {
            directBusList
                .distinctBy {
                    it.sourceBusStopCode + "->" + it.destinationBusStopCode
                }
                .forEach {
                    localDataSource.deleteDirectBuses(
                        sourceBusStopCode = it.sourceBusStopCode,
                        destinationBusStopCode = it.destinationBusStopCode,
                    )
                }

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
            localDataSource.deleteDirectBuses(
                sourceBusStopCode = sourceBusStopCode,
                destinationBusStopCode = destinationBusStopCode,
            )

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
                directBusEntityList.find { !it.hasDirectBus } != null -> {
                    DirectBusesResult.NoDirectBuses
                }
                else -> {
                    val sourceBusStop = getBusStop(busStopCode = sourceBusStopCode)
                        ?: throw Exception("No bus stop found for code $sourceBusStopCode")
                    val destinationBusStop = getBusStop(busStopCode = destinationBusStopCode)
                        ?: throw Exception("No bus stop found for code $sourceBusStopCode")

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

    override suspend fun getCachedDirectBusesStopPermutationsCount(): Int {
        return withContext(dispatcherProvider.io) {
            localDataSource
                .findAllDirectBuses()
                .distinctBy {
                    it.sourceBusStopCode + "->" + it.destinationBusStopCode
                }
                .size
        }
    }
}