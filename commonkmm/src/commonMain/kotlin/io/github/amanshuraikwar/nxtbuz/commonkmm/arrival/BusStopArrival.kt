package io.github.amanshuraikwar.nxtbuz.commonkmm.arrival

data class BusStopArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopDescription: String,
    val operator: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val busArrivals: BusArrivals
)