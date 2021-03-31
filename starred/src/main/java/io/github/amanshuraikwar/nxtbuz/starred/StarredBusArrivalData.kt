package io.github.amanshuraikwar.nxtbuz.starred

import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

data class StarredBusArrivalData(
    val busStopDescription: String,
    val busStopCode: String,
    val busServiceNumber: String,
    val arrivals: Arrivals,
)