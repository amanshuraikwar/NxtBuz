package io.github.amanshuraikwar.nxtbuz.commonkmm.arrival

data class BusServiceArrivalsLoopData(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopArrival: BusStopArrival
)