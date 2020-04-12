package io.github.amanshuraikwar.nxtbuz.data.busarrival.model

data class StarredBusArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val busStopDescription: String,
    val arrivals: Arrivals
)