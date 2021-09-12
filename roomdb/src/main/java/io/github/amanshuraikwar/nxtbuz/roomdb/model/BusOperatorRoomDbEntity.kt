package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity
import org.threeten.bp.OffsetDateTime

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(
    primaryKeys = ["busServiceNumber", "busStopCode"],
    tableName = "BusOperatorEntity"
)
data class BusOperatorRoomDbEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val operator: String,
    val lastUpdatedOn: OffsetDateTime = OffsetDateTime.now()
)