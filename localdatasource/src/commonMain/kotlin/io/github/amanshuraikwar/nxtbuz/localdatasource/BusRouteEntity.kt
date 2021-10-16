package io.github.amanshuraikwar.nxtbuz.localdatasource

data class BusRouteEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
)