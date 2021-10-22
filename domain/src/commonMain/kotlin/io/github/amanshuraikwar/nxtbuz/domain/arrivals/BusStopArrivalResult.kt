package io.github.amanshuraikwar.nxtbuz.domain.arrivals

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusStopArrival

data class BusStopArrivalResult(
    val busStopArrival: BusStopArrival,
    val isStarred: Boolean
)