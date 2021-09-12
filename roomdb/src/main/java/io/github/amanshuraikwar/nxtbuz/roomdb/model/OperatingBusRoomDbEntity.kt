package io.github.amanshuraikwar.nxtbuz.roomdb.model

import androidx.annotation.RestrictTo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetTime

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Entity(tableName = "OperatingBusEntity")
data class OperatingBusRoomDbEntity(
    val busStopCode: String,
    val busServiceNumber: String,
    val wdFirstBus: OffsetTime?,
    val wdLastBus: OffsetTime?,
    val satFirstBus: OffsetTime?,
    val satLastBus: OffsetTime?,
    val sunFirstBus: OffsetTime?,
    val sunLastBus: OffsetTime?,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)