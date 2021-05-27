package io.github.amanshuraikwar.nxtbuz.domain.busarrival

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusStopArrival

data class BusServiceArrivalsLoopData(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopArrival: BusStopArrival
)