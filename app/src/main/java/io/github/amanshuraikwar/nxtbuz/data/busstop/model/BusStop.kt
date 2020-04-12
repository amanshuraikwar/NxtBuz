package io.github.amanshuraikwar.nxtbuz.data.busstop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusStop(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val operatingBusList: List<Bus>
) : Parcelable

