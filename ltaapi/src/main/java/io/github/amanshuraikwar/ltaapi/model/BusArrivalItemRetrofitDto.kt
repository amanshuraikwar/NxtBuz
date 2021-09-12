package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

internal data class BusArrivalItemRetrofitDto(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("NextBus") val arrivingBus: ArrivingBusItemRetrofitDto?,
    @SerializedName("NextBus2") val arrivingBus1: ArrivingBusItemRetrofitDto?,
    @SerializedName("NextBus3") val arrivingBus2: ArrivingBusItemRetrofitDto?
)