package io.github.amanshuraikwar.nxtbuz.common.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BusRouteEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)