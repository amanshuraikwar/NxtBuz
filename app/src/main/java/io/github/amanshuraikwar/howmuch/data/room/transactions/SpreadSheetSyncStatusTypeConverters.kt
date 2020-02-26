package io.github.amanshuraikwar.howmuch.data.room.transactions

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.format.DateTimeFormatter

object SpreadSheetSyncStatusTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toSpreadSheetSyncStatus(ordinal: Int) = SpreadSheetSyncStatus.values()[ordinal]

    @TypeConverter
    @JvmStatic
    fun toOrdinal(spreadSheetSyncStatus: SpreadSheetSyncStatus) = spreadSheetSyncStatus.ordinal
}