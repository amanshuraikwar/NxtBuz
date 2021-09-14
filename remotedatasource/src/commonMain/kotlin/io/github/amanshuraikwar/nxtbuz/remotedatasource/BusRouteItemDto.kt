package io.github.amanshuraikwar.nxtbuz.remotedatasource

data class BusRouteItemDto(
    val serviceNumber: String,
    val operator: String,
    val direction: Int,
    val stopSequence: Int,
    val busStopCode: String,
    val distance: Double,
    val wdFirstBus: String,
    val wdLastBus: String,
    val satFirstBus: String,
    val satLastBus: String,
    val sunFirstBus: String,
    val sunLastBus: String
)