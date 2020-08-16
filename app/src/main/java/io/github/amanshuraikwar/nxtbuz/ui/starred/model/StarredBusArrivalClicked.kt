package io.github.amanshuraikwar.nxtbuz.ui.starred.model

import android.os.Parcelable
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StarredBusArrivalClicked(
    val busStop: BusStop,
    val busServiceNumber: String
) : Parcelable