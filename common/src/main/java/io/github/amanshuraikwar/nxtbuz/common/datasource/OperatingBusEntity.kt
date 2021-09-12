package io.github.amanshuraikwar.nxtbuz.common.datasource

import org.threeten.bp.OffsetTime

data class OperatingBusEntity(
    val busStopCode: String,
    val busServiceNumber: String,
    val wdFirstBus: OffsetTime?,
    val wdLastBus: OffsetTime?,
    val satFirstBus: OffsetTime?,
    val satLastBus: OffsetTime?,
    val sunFirstBus: OffsetTime?,
    val sunLastBus: OffsetTime?,
)