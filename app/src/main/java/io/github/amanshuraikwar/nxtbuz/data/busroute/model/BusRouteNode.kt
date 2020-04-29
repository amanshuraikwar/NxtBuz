package io.github.amanshuraikwar.nxtbuz.data.busroute.model

data class BusRouteNode(
    val busServiceNumber: String,
    val busStopCode: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val busStopLat: Double,
    val busStopLng: Double
)