package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(
    primaryKeys = ["busServiceNumber", "busStopCode"],
    tableName = "BusOperatorEntity"
)
internal data class BusOperatorRoomDbEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val operator: String,
    val lastUpdatedOn: Instant = Clock.System.now()
)