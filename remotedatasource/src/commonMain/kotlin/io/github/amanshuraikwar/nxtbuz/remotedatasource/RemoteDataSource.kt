package io.github.amanshuraikwar.nxtbuz.remotedatasource

interface RemoteDataSource {
    suspend fun getBusStops(skip: Int = 0): List<BusStopItemDto>

    suspend fun getBusArrivals(
        busStopCode: String,
        busServiceNumber: String? = null
    ): BusArrivalsResponseDto

    suspend fun getBusRoutes(skip: Int = 0): List<BusRouteItemDto>

    suspend fun getTrainStations(skip: Int = 0): List<StationItemDto>
}