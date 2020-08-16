package io.github.amanshuraikwar.nxtbuz.common.model

data class StarredBusArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopDescription: String,
    val arrivals: Arrivals
)