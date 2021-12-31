package io.github.amanshuraikwar.nxtbuz.sqldelightdb

import com.squareup.sqldelight.db.SqlDriver
import io.github.amanshuraikwar.nxtbuz.commonkmm.toSearchDescriptionHint
import io.github.amanshuraikwar.nxtbuz.db.NxtBuzDb
import io.github.amanshuraikwar.nxtbuz.localdatasource.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.math.max
import io.github.amanshuraikwar.nxtbuz.db.BusStopEntity as BusStopSqlDelightEntity
import io.github.amanshuraikwar.nxtbuz.db.StarredBusServiceEntity as StarredBusServiceSqlDelightEntity
import io.github.amanshuraikwar.nxtbuz.db.DirectBusEntity as DirectBusSqlDelightEntity

class SqlDelightLocalDataSource internal constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val nxtBuzDb: NxtBuzDb
) : LocalDataSource {
    override suspend fun insertBusStops(busStopList: List<BusStopEntity>) {
        withContext(ioDispatcher) {
            nxtBuzDb.busStopEntityQueries.transaction {
                busStopList
                    .distinctBy { it.code }
                    .forEach {
                        nxtBuzDb.busStopEntityQueries.insert(
                            BusStopSqlDelightEntity(
                                it.code,
                                it.roadName,
                                it.description,
                                it.description.toSearchDescriptionHint(),
                                it.latitude,
                                it.longitude
                            )
                        )
                    }
            }
        }
    }

    override suspend fun deleteAllBusStops() {
        withContext(ioDispatcher) {
            nxtBuzDb.busStopEntityQueries.deleteAll()
        }
    }

    override suspend fun findCloseBusStops(
        lat: Double,
        lng: Double,
        limit: Int
    ): List<BusStopEntity> {
        return withContext(ioDispatcher) {
            nxtBuzDb.busStopEntityQueries
                .findClose(
                    latitude = lat,
                    longitude = lng,
                    limit = limit.toLong()
                )
                .executeAsList()
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
            return@withContext nxtBuzDb.busStopEntityQueries
                .searchLikeDescription(
                    descriptionHint = descriptionHint,
                    limit = limit.toLong()
                )
                .executeAsList()
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
            nxtBuzDb.busStopEntityQueries
                .findByCode(
                    code = busStopCode,
                )
                .executeAsOneOrNull()
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
            nxtBuzDb.operatingBusEntityQueries.transaction {
                operatingBusList
                    .forEach {
                        nxtBuzDb.operatingBusEntityQueries.insert(
                            busStopCode = it.busStopCode,
                            busServiceNumber = it.busServiceNumber,
                            wdFirstBus = it.wdFirstBus?.toString(),
                            wdLastBus = it.wdLastBus?.toString(),
                            satFirstBus = it.satFirstBus?.toString(),
                            satLastBus = it.satLastBus?.toString(),
                            sunFirstBus = it.sunFirstBus?.toString(),
                            sunLastBus = it.sunLastBus?.toString(),
                        )
                    }
            }
        }
    }

    override suspend fun findOperatingBuses(busStopCode: String): List<OperatingBusEntity> {
        return withContext(ioDispatcher) {
            nxtBuzDb.operatingBusEntityQueries
                .findByBusStopCode(busStopCode)
                .executeAsList()
                .map { operatingBusEntity ->
                    OperatingBusEntity(
                        busStopCode = operatingBusEntity.busStopCode,
                        busServiceNumber = operatingBusEntity.busServiceNumber,
                        wdFirstBus = operatingBusEntity.wdFirstBus?.toLocalHourMinute(),
                        wdLastBus = operatingBusEntity.wdLastBus?.toLocalHourMinute(),
                        satFirstBus = operatingBusEntity.satFirstBus?.toLocalHourMinute(),
                        satLastBus = operatingBusEntity.satLastBus?.toLocalHourMinute(),
                        sunFirstBus = operatingBusEntity.sunFirstBus?.toLocalHourMinute(),
                        sunLastBus = operatingBusEntity.sunLastBus?.toLocalHourMinute(),
                    )
                }
        }
    }

    override suspend fun findOperatingBus(
        busStopCode: String,
        busServiceNumber: String
    ): OperatingBusEntity? {
        return withContext(ioDispatcher) {
            nxtBuzDb.operatingBusEntityQueries
                .findByBusStopCodeAndBusServiceNumber(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
                .executeAsOneOrNull()
                ?.let { operatingBusEntity ->
                    OperatingBusEntity(
                        busStopCode = operatingBusEntity.busStopCode,
                        busServiceNumber = operatingBusEntity.busServiceNumber,
                        wdFirstBus = operatingBusEntity.wdFirstBus?.toLocalHourMinute(),
                        wdLastBus = operatingBusEntity.wdLastBus?.toLocalHourMinute(),
                        satFirstBus = operatingBusEntity.satFirstBus?.toLocalHourMinute(),
                        satLastBus = operatingBusEntity.satLastBus?.toLocalHourMinute(),
                        sunFirstBus = operatingBusEntity.sunFirstBus?.toLocalHourMinute(),
                        sunLastBus = operatingBusEntity.sunLastBus?.toLocalHourMinute(),
                    )
                }
        }
    }

    override suspend fun deleteAllOperatingBuses() {
        withContext(ioDispatcher) {
            nxtBuzDb.operatingBusEntityQueries.deleteAll()
        }
    }

    override suspend fun insertBusRoute(busRouteList: List<BusRouteEntity>) {
        withContext(ioDispatcher) {
            nxtBuzDb.busRouteEntityQueries.transaction {
                busRouteList.forEach { busRouteEntity ->
                    nxtBuzDb.busRouteEntityQueries.insert(
                        busServiceNumber = busRouteEntity.busServiceNumber,
                        busStopCode = busRouteEntity.busStopCode,
                        direction = busRouteEntity.direction.toLong(),
                        stopSequence = busRouteEntity.stopSequence.toLong(),
                        distance = busRouteEntity.distance
                    )
                }
            }
        }
    }

    override suspend fun findBusRoute(
        busStopCode: String,
        busServiceNumber: String
    ): BusRouteEntity? {
        return withContext(ioDispatcher) {
            nxtBuzDb.busRouteEntityQueries
                .findByBusServiceNumberAndBusStopCode(
                    busServiceNumber = busServiceNumber,
                    busStopCode = busStopCode
                )
                .executeAsList()
                .getOrNull(0)
                ?.let { busRouteEntity ->
                    BusRouteEntity(
                        busServiceNumber = busRouteEntity.busServiceNumber,
                        busStopCode = busRouteEntity.busStopCode,
                        direction = busRouteEntity.direction.toInt(),
                        stopSequence = busRouteEntity.stopSequence.toInt(),
                        distance = busRouteEntity.distance
                    )
                }
        }
    }

    override suspend fun findBusRoute(busServiceNumber: String): List<BusRouteEntity> {
        return withContext(ioDispatcher) {
            nxtBuzDb.busRouteEntityQueries
                .findByBusServiceNumber(busServiceNumber)
                .executeAsList()
                .map {
                    BusRouteEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode,
                        direction = it.direction.toInt(),
                        stopSequence = it.stopSequence.toInt(),
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
            val queryResult = nxtBuzDb.busRouteEntityQueries
                .searchLikeBusServiceNumberAllOrder(
                    busServiceNumber = busServiceNumberHint,
                )
                .executeAsList()
                .groupBy { it.busServiceNumber }
                .map { (_, v) -> v.sortedBy { it.direction }[0] }

            queryResult
                .dropLast(max(0, queryResult.size - limit))
                .map {
                    BusRouteEntity(
                        busServiceNumber = it.busServiceNumber,
                        busStopCode = it.busStopCode,
                        direction = it.direction.toInt(),
                        stopSequence = it.stopSequence.toInt(),
                        distance = it.distance
                    )
                }
        }
    }

    override suspend fun deleteAllBusRoutes() {
        withContext(ioDispatcher) {
            nxtBuzDb.busRouteEntityQueries.deleteAll()
        }
    }

    override suspend fun insertStarredBuses(starredBusList: List<StarredBusStopEntity>) {
        withContext(ioDispatcher) {
            starredBusList.forEach {
                nxtBuzDb.starredBusServiceEntityQueries
                    .insert(
                        StarredBusServiceSqlDelightEntity(
                            busServiceNumber = it.busServiceNumber,
                            busStopCode = it.busStopCode
                        )
                    )
            }
        }
    }

    override suspend fun findStarredBuses(busStopCode: String): List<StarredBusStopEntity> {
        return withContext(ioDispatcher) {
            nxtBuzDb.starredBusServiceEntityQueries
                .findByBusStopCode(busStopCode = busStopCode)
                .executeAsList()
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
            nxtBuzDb.starredBusServiceEntityQueries
                .findByBusStopCodeAndBusServiceNumber(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
                .executeAsOneOrNull()
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
            nxtBuzDb.starredBusServiceEntityQueries
                .deleteByBusStopCodeAndBusServiceNumber(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber
                )
        }
    }

    override suspend fun findAllStarredBuses(): List<StarredBusStopEntity> {
        return withContext(ioDispatcher) {
            nxtBuzDb.starredBusServiceEntityQueries
                .findAll()
                .executeAsList()
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
        return withContext(ioDispatcher) {
            nxtBuzDb
                .directBusEntityQueries
                .findBySourceAndDenstinationBusStopCode(
                    sourceBusStopCode = sourceBusStopCode,
                    destinationBusStopCode = destinationBusStopCode
                )
                .executeAsList()
                .map {
                    DirectBusEntity(
                        sourceBusStopCode = it.sourceBusStopCode,
                        destinationBusStopCode = it.destinationBusStopCode,
                        hasDirectBus = it.hasDirectBus == 1L,
                        busServiceNumber = it.busServiceNumber,
                        stops = it.stops.toInt(),
                        distance = it.distance
                    )
                }
        }
    }

    override suspend fun insertDirectBuses(directBusList: List<DirectBusEntity>) {
        withContext(ioDispatcher) {
            nxtBuzDb.directBusEntityQueries.transaction {
                directBusList.forEach {
                    nxtBuzDb
                        .directBusEntityQueries
                        .insert(
                            DirectBusSqlDelightEntity(
                                sourceBusStopCode = it.sourceBusStopCode,
                                destinationBusStopCode = it.destinationBusStopCode,
                                hasDirectBus = if (it.hasDirectBus) 1L else 0L,
                                busServiceNumber = it.busServiceNumber,
                                stops = it.stops.toLong(),
                                distance = it.distance
                            )
                        )
                }
            }
        }
    }

    override suspend fun findAllDirectBuses(): List<DirectBusEntity> {
        return withContext(ioDispatcher) {
            nxtBuzDb.directBusEntityQueries
                .findAll()
                .executeAsList()
                .map {
                    DirectBusEntity(
                        sourceBusStopCode = it.sourceBusStopCode,
                        destinationBusStopCode = it.destinationBusStopCode,
                        hasDirectBus = it.hasDirectBus == 1L,
                        busServiceNumber = it.busServiceNumber,
                        stops = it.stops.toInt(),
                        distance = it.distance
                    )
                }
        }
    }

    companion object {
        fun createInstance(
            dbFactory: DbFactory,
            ioDispatcher: CoroutineDispatcher
        ): LocalDataSource {
            return SqlDelightLocalDataSource(
                ioDispatcher = ioDispatcher,
                nxtBuzDb = dbFactory.createDb()
            )
        }

        fun createInstance(
            driver: SqlDriver,
            ioDispatcher: CoroutineDispatcher
        ): LocalDataSource {
            return SqlDelightLocalDataSource(
                ioDispatcher = ioDispatcher,
                nxtBuzDb = NxtBuzDb(driver)
            )
        }
    }
}