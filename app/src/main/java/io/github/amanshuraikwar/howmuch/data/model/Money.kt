package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import io.github.amanshuraikwar.howmuch.util.money
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Money(
    private val amountStr: String,
    val amount: Double = amountStr.money()
) : Parcelable