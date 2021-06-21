package io.github.amanshuraikwar.nxtbuz.common.model.arrival

data class BusServiceArrivalsLoopData(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopArrival: BusStopArrival
)