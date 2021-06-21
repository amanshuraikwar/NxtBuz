package io.github.amanshuraikwar.nxtbuz.common.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bus(
    val serviceNumber: String
) : Parcelable