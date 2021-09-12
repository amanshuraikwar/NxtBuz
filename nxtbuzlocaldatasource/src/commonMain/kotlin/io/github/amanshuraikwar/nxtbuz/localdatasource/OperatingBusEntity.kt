package io.github.amanshuraikwar.nxtbuz.localdatasource

data class OperatingBusEntity(
    val busStopCode: String,
    val busServiceNumber: String,
    val wdFirstBus: LocalHourMinute?,
    val wdLastBus: LocalHourMinute?,
    val satFirstBus: LocalHourMinute?,
    val satLastBus: LocalHourMinute?,
    val sunFirstBus: LocalHourMinute?,
    val sunLastBus: LocalHourMinute?,
)