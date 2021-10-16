package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BusRouteItemDto(
    @SerialName("ServiceNo") val serviceNumber: String,
    @SerialName("Operator") val operator: String,
    @SerialName("Direction") val direction: Int,
    @SerialName("StopSequence") val stopSequence: Int,
    @SerialName("BusStopCode") val busStopCode: String,
    @SerialName("Distance") val distance: Double?,
    @SerialName("WD_FirstBus") val wdFirstBus: String,
    @SerialName("WD_LastBus") val wdLastBus: String,
    @SerialName("SAT_FirstBus") val satFirstBus: String,
    @SerialName("SAT_LastBus") val satLastBus: String,
    @SerialName("SUN_FirstBus") val sunFirstBus: String,
    @SerialName("SUN_LastBus") val sunLastBus: String
)