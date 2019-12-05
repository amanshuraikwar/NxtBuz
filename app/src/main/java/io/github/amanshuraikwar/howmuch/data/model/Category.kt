package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val cell: SpreadSheetCell,
    var name: String,
    val monthlyLimit: Money,
    val id: String = cell.toString()
) : Parcelable