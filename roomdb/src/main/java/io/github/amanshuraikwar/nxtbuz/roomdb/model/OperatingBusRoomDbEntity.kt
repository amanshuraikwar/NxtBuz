package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalHourMinute

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = "OperatingBusEntity")
data class OperatingBusRoomDbEntity(
    val busStopCode: String,
    val busServiceNumber: String,
    val wdFirstBus: LocalHourMinute?,
    val wdLastBus: LocalHourMinute?,
    val satFirstBus: LocalHourMinute?,
    val satLastBus: LocalHourMinute?,
    val sunFirstBus: LocalHourMinute?,
    val sunLastBus: LocalHourMinute?,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)