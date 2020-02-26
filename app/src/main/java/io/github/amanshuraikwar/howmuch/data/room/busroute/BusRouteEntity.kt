package io.github.amanshuraikwar.howmuch.data.room.busroute

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.amanshuraikwar.howmuch.data.BusServiceNumber
import io.github.amanshuraikwar.howmuch.data.BusStopCode

@Entity
data class BusRouteEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)