package io.github.amanshuraikwar.nxtbuz.data.busarrival.model

import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalEntity

data class BusArrivalsState(
    val busStopCode: String,
    val busArrivalEntityList: List<BusArrivalEntity>
)