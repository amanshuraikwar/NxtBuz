package io.github.amanshuraikwar.nxtbuz.common.model.room

import androidx.room.Entity
import org.threeten.bp.OffsetDateTime

@Entity(primaryKeys = ["busServiceNumber", "busStopCode"])
data class BusOperatorEntity(
    val busServiceNumber: String,
    val busStopCode: String,
    val operator: String,
    val lastUpdatedOn: OffsetDateTime = OffsetDateTime.now()
)