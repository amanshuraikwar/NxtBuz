package io.github.amanshuraikwar.howmuch.data.room.starredbusstops

import androidx.room.Entity
import io.github.amanshuraikwar.howmuch.data.BusStopCode

@Entity(primaryKeys = ["busStopCode", "busServiceNumber"])
data class StarredBusStopEntity(
    val busStopCode: String,
    val busServiceNumber: String
)