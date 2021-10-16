package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BusStopItemDto(
    @SerialName("BusStopCode") val code: String,
    @SerialName("RoadName") val roadName: String,
    @SerialName("Description") val description: String,
    @SerialName("Latitude") val latitude: Double,
    @SerialName("Longitude") val longitude: Double
)