package io.github.amanshuraikwar.nxtbuz.commonkmm.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals

data class StarredBusArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopDescription: String,
    val busArrivals: BusArrivals
)