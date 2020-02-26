package io.github.amanshuraikwar.howmuch.data.room.operatingbus

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.amanshuraikwar.howmuch.data.BusServiceNumber
import io.github.amanshuraikwar.howmuch.data.BusStopCode
import org.threeten.bp.OffsetTime

@Entity
data class OperatingBusEntity(
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