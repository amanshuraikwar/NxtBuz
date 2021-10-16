package io.github.amanshuraikwar.nxtbuz.iosumbrella.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusArrivals

data class IosBusStopArrival(
    val busStopCode: String,
    val busServiceNumber: String,
    val operator: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val starred: Boolean,
    val busArrivals: BusArrivals
)