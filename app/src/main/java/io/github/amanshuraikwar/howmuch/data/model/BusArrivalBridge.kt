package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusArrivalBridge(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("NextBus") val arrivingBus: ArrivingBus,
    @SerializedName("NextBus1") val arrivingBus1: ArrivingBus,
    @SerializedName("NextBus2") val arrivingBus2: ArrivingBus
) : Parcelable

fun BusArrivalBridge.asBusArrival(): BusArrival = BusArrival(
    serviceNumber,
    operator,
    this.let {
        val arrivals = mutableListOf<ArrivingBus>()
        arrivingBus?.let { arrivals.add(it) }
        arrivingBus1?.let { arrivals.add(it) }
        arrivingBus2?.let { arrivals.add(it) }
        arrivals
    }
)