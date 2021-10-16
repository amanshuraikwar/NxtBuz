package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ArrivingBusItemDto(
    @SerialName("OriginCode") val originCode: String,
    @SerialName("DestinationCode") val destinationCode: String,
    @SerialName("EstimatedArrival") val estimatedArrival: String,
    @SerialName("Latitude") val latitude: String,
    @SerialName("Longitude") val longitude: String,
    @SerialName("VisitNumber") val visitNumber: String,
    @SerialName("Load") val load: String,
    @SerialName("Feature") val feature: String,
    @SerialName("Type") val type: String
)