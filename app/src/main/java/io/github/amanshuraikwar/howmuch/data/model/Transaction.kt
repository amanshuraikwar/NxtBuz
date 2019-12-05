package io.github.amanshuraikwar.howmuch.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
data class Transaction(
    val cell: SpreadSheetCell,
    var datetime: OffsetDateTime,
    var amount: Money,
    var title: String,
    val category: Category,
    val id: String = cell.toString()
) : Parcelable