package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity
import androidx.room.PrimaryKey

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = "BusRouteEntity")
internal data class BusRouteRoomDbEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)