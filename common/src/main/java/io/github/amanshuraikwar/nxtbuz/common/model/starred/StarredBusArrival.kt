package io.github.amanshuraikwar.nxtbuz.common.model.starred

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals

data class StarredBusArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopDescription: String,
    val busArrivals: BusArrivals
)