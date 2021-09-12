package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

internal data class BusRouteItemRetrofitDto(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("Direction") val direction: Int,
    @SerializedName("StopSequence") val stopSequence: Int,
    @SerializedName("BusStopCode") val busStopCode: String,
    @SerializedName("Distance") val distance: Double,
    @SerializedName("WD_FirstBus") val wdFirstBus: String,
    @SerializedName("WD_LastBus") val wdLastBus: String,
    @SerializedName("SAT_FirstBus") val satFirstBus: String,
    @SerializedName("SAT_LastBus") val satLastBus: String,
    @SerializedName("SUN_FirstBus") val sunFirstBus: String,
    @SerializedName("SUN_LastBus") val sunLastBus: String
)