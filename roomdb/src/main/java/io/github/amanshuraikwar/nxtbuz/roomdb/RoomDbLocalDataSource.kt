package io.github.amanshuraikwar.nxtbuz.roomdb

import android.content.Context
import io.github.amanshuraikwar.nxtbuz.localdatasource.*
import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusRouteRoomDbEntity
import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusStopRoomDbEntity
import io.github.amanshuraikwar.nxtbuz.roomdb.model.OperatingBusRoomDbEntity
import io.github.amanshuraikwar.nxtbuz.roomdb.model.StarredBusStopRoomDbEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RoomDbLocalDataSource internal constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val appDatabase: AppDatabase,
) : LocalDataSource {
    override suspend fun insertBusStops(busStopList: List<BusStopEntity>) {
        withContext(ioDispatcher) {
            // save all bus stops in local db
            appDatabase.busStopDao.insertAll(
                busStopList
                    .distinctBy { it.code }
                    .map {
                        BusStopRoomDbEntity(
                            it.code,
                            it.roadName,
                            it.description,
                            it.latitude,
                            it.longitude
                        )
                    }
            )
        }
    }

    override suspend fun deleteAllBusStops() {
        withContext(ioDispatcher) {
            appDatabase.busStopDao.deleteAll()
        }
    }

    override suspend fun findCloseBusStops(
        lat: Double,
        lng: Double,
        limit: Int
    ): List<BusStopEntity> {
        return withContext(ioDispatcher) {
            appDatabase.busStopDao
                .findClose(
                    latitude = lat,
                    longitude = lng,
                    limit = limit
                )
                .map { busStopEntity ->
                    BusStopEntity(
                        busStopEntity.code,
                        busStopEntity.roadName,
                        busStopEntity.description,
                        busStopEntity.latitude,
                        busStopEntity.longitude,
                    )
                }
        }
    }

    override suspend fun findBusStopsByDescription(
        descriptionHint: String,
        limit: Int
    ): List<BusStopEntity> {
        return withContext(ioDispatcher) {
            appDatabase.busStopDao
                .searchLikeDescription(
                    description = descriptionHint,
                    limit = limit
                )
                .map { busStopEntity ->
                    BusStopEntity(
                        busStopEntity.code,
                        busStopEntity.roadName,
                        busStopEntity.description,
                        busStopEntity.latitude,
                        busStopEntity.longitude,
                    )
                }
        }
    }

    override suspend fun findBusStopByCode(busStopCode: String): BusStopEntity? {
        return withContext(ioDispatcher) {
            appDatabase.busStopDao
                .findByCode(
                    code = busStopCode,
                )
                .getOrNull(0)
                ?.let { busStopEntity ->
                    BusStopEntity(
                        busStopEntity.code,
                        busStopEntity.roadName,
                        busStopEntity.description,
                        busStopEntity.latitude,
                        busStopEntity.longitude,
                    )
                }
        }
    }

    override suspend fun insertOperatingBuses(operatingBusList: List<OperatingBusEntity>) {
        withContext(ioDispatcher) {
            appDatabase.operatingBusDao.insertAll(
                operatingBusList.map {
                    OperatingBusRoomDbEntity(
                        busStopCode = it.busStopCode,
                        busServiceNumber = it.busServiceNumber,
                        wdFirstBus = it.wdFirstBus,
                        wdLastBus = it.wdLastBus,
                        satFirstBus = it.satFirstBus,
                        satLastBus = it.satLastBus,
                        sunFirstBus = it.sunFirstBus,
                        sunLastBus = it.sunLastBus
                    )
                }
            )
        }
    }

    override suspend fun findOperatingBuses(busStopCode: String): List<OperatingBusEntity> {
        return withContext(ioDispatcher) {
            appDatabase.operatingBusDao
                .findByBusStopCode(busStopCode)
                .map { operatingBusEntity ->
                    OperatingBusEntity(
                        busStopCode = operatingBusEntity.busStopCode,
                        busServiceNumber = operatingBusEntity.busServiceNumber,
                        wdFirstBus = operatingBusEntity.wdFirstBus,
                        wdLastBus = operatingBusEntity.wdLastBus,
                        satFirstBus = operatingBusEntity.satFirstBus,
                        satLastBus = operatingBusEntity.satLastBus,
                        sunFirstBus = operatingBusEntity.sunFirstBus,
                        sunLastBus = operatingBusEntity.sunLastBus,
                    )
                }
        }
    }

    override suspend fun findOperatingBus(
        busStopCode: String,
        busServiceNumber: String
    ): OperatingBusEntity? {
        return withContext(ioDispatcher) {
            appDatabase.operatingBusDao
                .findByBusStopCodeAndBusServiceNumber(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
                .getOrNull(0)
                ?.let { operatingBusEntity ->
                    OperatingBusEntity(
                        busStopCode = operatingBusEntity.busStopCode,
                        busServiceNumber = operatingBusEntity.busServiceNumber,
                        wdFirstBus = operatingBusEntity.wdFirstBus,
                        wdLastBus = operatingBusEntity.wdLastBus,
                        satFirstBus = operatingBusEntity.satFirstBus,
                        satLastBus = operatingBusEntity.satLastBus,
                        sunFirstBus = operatingBusEntity.sunFirstBus,
                        sunLastBus = operatingBusEntity.sunLastBus,
                    )
                }
        }
    }

    override suspend fun deleteAllOperatingBuses() {
        withContext(ioDispatcher) {
            appDatabase.operatingBusDao.deleteAll()
        }
    }

    override suspend fun insertBusRoute(busRouteList: List<BusRouteEntity>) {
        withContext(ioDispatcher) {
            appDatabase.busRouteDao.insertAll(
                busRouteList.map { busRouteEntity ->
                    BusRouteRoomDbEntity(
                        busServiceNumber = busRouteEntity.busServiceNumber,
                        busStopCode = busRouteEntity.busStopCode,
                        direction = busRouteEntity.direction,
                        stopSequence = busRouteEntity.stopSequence,
                        distance = busRouteEntity.distance
                    )
                }
            )
        }
    }

    override suspend fun findBusRoute(
        busStopCode: String,
        busServiceNumber: String
    ): BusRouteEntity? {
        return withContext(ioDispatcher) {
            appDatabase.busRouteDao
                .findByBusServiceNumberAndBusStopCode(
                    busServiceNumber = busServiceNumber,
                    busStopCode = busStopCode
                )
                .getOrNull(0)
                ?.let { busRouteEntity ->
                    BusRouteEntity(
                        busServiceNumber = busRouteEntity.busServiceNumber,
                        busStopCode = busRouteEntity.busStopCode,
                        direction = busRouteEntity.direction,
                        stopSequence = busRouteEntity.stopSequence,
                        distance = busRouteEntity.distance
                    )
                }
        }
    }

    override suspend fun findBusRoute(busServiceNumber: String): List<BusRouteEntity> {
        return withContext(ioDispatcher) {
            appDatabase.busRouteDao
                .findByBusServiceNumber(busServiceNumber)
                .map {
                    BusRouteEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode,
                        direction = it.direction,
                        stopSequence = it.stopSequence,
                        distance = it.distance
                    )
                }
        }
    }

    override suspend fun findBusRouteByBusServiceNumber(
        busServiceNumberHint: String,
        limit: Int
    ): List<BusRouteEntity> {
        return withContext(ioDispatcher) {
            appDatabase.busRouteDao
                .searchLikeBusServiceNumber(
                    busServiceNumber = busServiceNumberHint,
                    limit = limit
                )
                .map {
                    BusRouteEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode,
                        direction = it.direction,
                        stopSequence = it.stopSequence,
                        distance = it.distance
                    )
                }
        }
    }

    override suspend fun deleteAllBusRoutes() {
        withContext(ioDispatcher) {
            appDatabase.busRouteDao.deleteAll()
        }
    }

    override suspend fun insertStarredBuses(starredBusList: List<StarredBusStopEntity>) {
        withContext(ioDispatcher) {
            appDatabase.starredBusStopsDao
                .insertAll(
                    starredBusList.map {
                        StarredBusStopRoomDbEntity(
                            busServiceNumber = it.busServiceNumber,
                            busStopCode = it.busStopCode
                        )
                    }
                )
        }
    }

    override suspend fun findStarredBuses(busStopCode: String): List<StarredBusStopEntity> {
        return withContext(ioDispatcher) {
            appDatabase.starredBusStopsDao
                .findByBusStopCode(busStopCode = busStopCode)
                .map {
                    StarredBusStopEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode
                    )
                }
        }
    }

    override suspend fun findStarredBus(
        busStopCode: String,
        busServiceNumber: String
    ): StarredBusStopEntity? {
        return withContext(ioDispatcher) {
            appDatabase.starredBusStopsDao
                .findByBusStopCodeAndBusServiceNumber(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
                .getOrNull(0)
                ?.let {
                    StarredBusStopEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode
                    )
                }
        }
    }

    override suspend fun deleteStarredBus(busStopCode: String, busServiceNumber: String) {
        withContext(ioDispatcher) {
            appDatabase
                .starredBusStopsDao
                .deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
        }
    }

    override suspend fun findAllStarredBuses(): List<StarredBusStopEntity> {
        return withContext(ioDispatcher) {
            appDatabase.starredBusStopsDao
                .findAll()
                .map {
                    StarredBusStopEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode
                    )
                }
        }
    }

    override suspend fun findDirectBuses(
        sourceBusStopCode: String,
        destinationBusStopCode: String
    ): List<DirectBusEntity> {
        return emptyList()
    }

    override suspend fun insertDirectBuses(directBusList: List<DirectBusEntity>) {
        // do nothing
    }

    override suspend fun findAllDirectBuses(): List<DirectBusEntity> {
        return emptyList()
    }

    companion object {
        fun createInstance(
            context: Context,
            ioDispatcher: CoroutineDispatcher
        ): LocalDataSource {
            return RoomDbLocalDataSource(
                ioDispatcher = ioDispatcher,
                AppDatabase.getInstance(context),
            )
        }
    }
}