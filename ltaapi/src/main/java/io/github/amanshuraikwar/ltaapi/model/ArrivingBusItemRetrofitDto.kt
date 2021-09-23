package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

internal data class ArrivingBusItemRetrofitDto(
    @SerializedName("OriginCode") val originCode: String,
    @SerializedName("DestinationCode") val destinationCode: String,
    @SerializedName("EstimatedArrival") val estimatedArrival: String,
    @SerializedName("Latitude") val latitude: String,
    @SerializedName("Longitude") val longitude: String,
    @SerializedName("VisitNumber") val visitNumber: String,
    @SerializedName("Load") val load: String,
    @SerializedName("Feature") val feature: String,
    @SerializedName("Type") val type: String
)