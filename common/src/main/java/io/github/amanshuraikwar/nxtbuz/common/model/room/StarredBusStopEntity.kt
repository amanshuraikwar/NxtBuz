package io.github.amanshuraikwar.nxtbuz.common.model.room

import androidx.room.Entity

@Entity(primaryKeys = ["busStopCode", "busServiceNumber"])
data class StarredBusStopEntity(
    val busStopCode: String,
    val busServiceNumber: String
)