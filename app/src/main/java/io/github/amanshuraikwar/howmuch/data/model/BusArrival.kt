package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusArrival(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    val arrivals: List<ArrivingBus>
) : Parcelable