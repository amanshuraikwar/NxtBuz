package io.github.amanshuraikwar.nxtbuz.data.busstop.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bus(
    val serviceNumber: String
    //val busOperator: String
) : Parcelable