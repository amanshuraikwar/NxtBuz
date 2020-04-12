package io.github.amanshuraikwar.nxtbuz.data.busapi.model

import com.google.gson.annotations.SerializedName

data class BusArrivalItem(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("NextBus") val arrivingBus: ArrivingBusItem?,
    @SerializedName("NextBus2") val arrivingBus1: ArrivingBusItem?,
    @SerializedName("NextBus3") val arrivingBus2: ArrivingBusItem?
)