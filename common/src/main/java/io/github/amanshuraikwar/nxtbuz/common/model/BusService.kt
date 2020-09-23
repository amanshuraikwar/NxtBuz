package io.github.amanshuraikwar.nxtbuz.common.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BusService(
    val busServiceNumber: String,
    val originBusStopDescription: String,
    val destinationBusStopDescription: String,
    val numberOfBusStops: Int,
    val distance: Double
) : Parcelable