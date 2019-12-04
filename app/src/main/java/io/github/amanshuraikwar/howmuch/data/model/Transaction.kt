package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
data class Transaction(
    val id: String,
    var datetime: OffsetDateTime,
    var amount: Money,
    var title: String,
    val category: Category
) : Parcelable