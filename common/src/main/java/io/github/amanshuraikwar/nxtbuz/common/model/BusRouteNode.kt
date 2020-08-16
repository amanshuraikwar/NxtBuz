package io.github.amanshuraikwar.nxtbuz.common.model

data class BusRouteNode(
    val busServiceNumber: String,
    val busStopCode: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val busStopRoadName: String,
    val busStopDescription: String,
    val busStopLat: Double,
    val busStopLng: Double
)