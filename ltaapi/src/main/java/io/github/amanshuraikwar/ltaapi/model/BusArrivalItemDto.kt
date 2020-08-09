package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

data class BusArrivalItemDto(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("NextBus") val arrivingBus: ArrivingBusItemDto?,
    @SerializedName("NextBus2") val arrivingBus1: ArrivingBusItemDto?,
    @SerializedName("NextBus3") val arrivingBus2: ArrivingBusItemDto?
)