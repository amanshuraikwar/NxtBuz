package io.github.amanshuraikwar.nxtbuz.common.datasource

interface LocalDataSource {
    suspend fun insertBusStops(busStopList: List<BusStopEntity>)

    suspend fun deleteAllBusStops()

    suspend fun findCloseBusStops(lat: Double, lng: Double, limit: Int): List<BusStopEntity>

    suspend fun findBusStopsByDescription(descriptionHint: String, limit: Int): List<BusStopEntity>

    suspend fun findBusStopByCode(busStopCode: String): BusStopEntity?

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

    suspend fun insertStarredBuses(starredBusList: List<StarredBusStopEntity>)

    suspend fun findStarredBuses(busStopCode: String): List<StarredBusStopEntity>

    suspend fun findStarredBus(busStopCode: String, busServiceNumber: String): StarredBusStopEntity?

    suspend fun deleteStarredBus(busStopCode: String, busServiceNumber: String)

    suspend fun findAllStarredBuses(): List<StarredBusStopEntity>
}