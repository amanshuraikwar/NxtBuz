package io.github.amanshuraikwar.nxtbuz.data.model

import android.os.Parcelable
import io.github.amanshuraikwar.nxtbuz.data.room.transactions.SpreadSheetSyncStatus
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.OffsetDateTime

@Parcelize
data class Transaction(
    var datetime: OffsetDateTime,
    var amount: Money,
    var title: String,
    val category: Category,
    val spreadSheetSyncStatus: SpreadSheetSyncStatus,
    val id: String = datetime.toInstant().toEpochMilli().toString()
) : Parcelable