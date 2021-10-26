package io.github.amanshuraikwar.nxtbuz.commonkmm.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals

data class StarredBusArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStop: BusStop,
    val busArrivals: BusArrivals
)