package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

data class BusStopItemDto(
    @SerializedName("BusStopCode") val code: String,
    @SerializedName("RoadName") val roadName: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Latitude") val latitude: Double,
    @SerializedName("Longitude") val longitude: Double
)