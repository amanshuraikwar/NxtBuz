package io.github.amanshuraikwar.nxtbuz.remotedatasource

data class BusArrivalsResponseDto(
    val busStopCode: Int,
    val busArrivals: List<BusArrivalItemDto>
)