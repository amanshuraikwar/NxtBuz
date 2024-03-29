package io.github.amanshuraikwar.nxtbuz.localdatasource

interface LocalDataSource {
    suspend fun insertBusStops(busStopList: List<BusStopEntity>)

    suspend fun deleteAllBusStops()

    suspend fun findCloseBusStops(lat: Double, lng: Double, limit: Int): List<BusStopEntity>

    suspend fun findBusStopsByDescription(descriptionHint: String, limit: Int): List<BusStopEntity>

    suspend fun findBusStopByCode(busStopCode: String): BusStopEntity?

    suspend fun getAllBusStops(): List<BusStopEntity>

    suspend fun updateBusStop(busStop: BusStopEntity)

    suspend fun findAllStarredBusStops(): List<BusStopEntity>

    suspend fun insertOperatingBuses(operatingBusList: List<OperatingBusEntity>)

    suspend fun findOperatingBuses(busStopCode: String): List<OperatingBusEntity>

    suspend fun findOperatingBus(busStopCode: String, busServiceNumber: String): OperatingBusEntity?

    suspend fun deleteAllOperatingBuses()

    suspend fun insertBusRoute(busRouteList: List<BusRouteEntity>)

    suspend fun findBusRoute(busStopCode: String, busServiceNumber: String): BusRouteEntity?

    suspend fun findBusRoute(busServiceNumber: String): List<BusRouteEntity>

    suspend fun findBusRouteByBusServiceNumber(
        busServiceNumberHint: String,
        limit: Int,
    ): List<BusRouteEntity>

    suspend fun deleteAllBusRoutes()

    //region starred buses
    suspend fun insertStarredBuses(starredBusList: List<StarredBusServiceEntity>)

    suspend fun findStarredBuses(busStopCode: String): List<StarredBusServiceEntity>

    suspend fun findStarredBus(
        busStopCode: String,
        busServiceNumber: String
    ): StarredBusServiceEntity?

    suspend fun deleteStarredBus(busStopCode: String, busServiceNumber: String)

    suspend fun findAllStarredBuses(): List<StarredBusServiceEntity>
    //endregion

    suspend fun findDirectBuses(
        sourceBusStopCode: String,
        destinationBusStopCode: String
    ): List<DirectBusEntity>

    suspend fun insertDirectBuses(directBusList: List<DirectBusEntity>)

    suspend fun findAllDirectBuses(): List<DirectBusEntity>

    suspend fun deleteDirectBuses(
        sourceBusStopCode: String,
        destinationBusStopCode: String,
    )

    suspend fun deleteDirectBuses(
        sourceBusStopCode: String,
        destinationBusStopCode: String,
        busServiceNumber: String
    )
}