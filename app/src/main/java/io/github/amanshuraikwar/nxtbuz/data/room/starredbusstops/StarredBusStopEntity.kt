package io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops

import androidx.room.Entity

@Entity(primaryKeys = ["busStopCode", "busServiceNumber"])
data class StarredBusStopEntity(
    val busStopCode: String,
    val busServiceNumber: String
)