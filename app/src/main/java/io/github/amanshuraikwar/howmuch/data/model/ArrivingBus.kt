package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArrivingBus(
    @SerializedName("OriginCode") val originCode: String,
    @SerializedName("DestinationCode") val destinationCode: String,
    @SerializedName("EstimatedArrival") val estimatedArrival: String,
    @SerializedName("Latitude") val latitude: String,
    @SerializedName("Longitude") val longitude: String,
    @SerializedName("VisitNUmber") val visitNumber: String,
    @SerializedName("Load") val load: String,
    @SerializedName("Feature") val feature: String,
    @SerializedName("Type") val type: String
) : Parcelable

enum class BusLoad {
    SEA, SDA, LSD
}

enum class BusType {
    SD, DD, BD
}