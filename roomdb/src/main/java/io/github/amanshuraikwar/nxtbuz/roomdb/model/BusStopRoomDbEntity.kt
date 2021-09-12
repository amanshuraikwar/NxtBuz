package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(
    primaryKeys = ["code"],
    tableName = "BusStopEntity"
)
internal data class BusStopRoomDbEntity(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)