package io.github.amanshuraikwar.nxtbuz.starred

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals

data class StarredBusArrivalData(
    val busStopDescription: String,
    val busStopCode: String,
    val busServiceNumber: String,
    val busArrivals: BusArrivals,
)