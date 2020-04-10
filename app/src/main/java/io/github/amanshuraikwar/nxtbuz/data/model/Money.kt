package io.github.amanshuraikwar.nxtbuz.data.model

import android.os.Parcelable
import io.github.amanshuraikwar.nxtbuz.util.money
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Money(
    private val amountStr: String,
    val amount: Double = amountStr.money()
) : Parcelable {
    constructor(inputAmount: Double) : this(inputAmount.toString())
    constructor(inputAmount: Float) : this(inputAmount.toString())
}