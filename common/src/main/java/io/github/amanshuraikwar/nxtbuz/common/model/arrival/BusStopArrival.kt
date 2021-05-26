package io.github.amanshuraikwar.nxtbuz.common.model.arrival

data class BusStopArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val operator: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val busArrivals: BusArrivals
)