package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import io.github.amanshuraikwar.howmuch.data.BusServiceNumber
import io.github.amanshuraikwar.howmuch.data.BusStopCode
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

@Parcelize
data class Bus(
    val serviceNumber: String
    //val busOperator: String
) : Parcelable