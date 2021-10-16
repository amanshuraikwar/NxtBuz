package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(
    primaryKeys = ["busStopCode", "busServiceNumber"],
    tableName = "StarredBusStopEntity"
)
internal data class StarredBusStopRoomDbEntity(
    val busStopCode: String,
    val busServiceNumber: String
)