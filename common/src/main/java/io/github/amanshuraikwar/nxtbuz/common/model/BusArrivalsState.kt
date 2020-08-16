package io.github.amanshuraikwar.nxtbuz.common.model

import io.github.amanshuraikwar.nxtbuz.common.model.room.BusArrivalEntity

data class BusArrivalsState(
    val busStopCode: String,
    val busArrivalEntityList: List<BusArrivalEntity>
)