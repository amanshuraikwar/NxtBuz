package io.github.amanshuraikwar.nxtbuz.data.room.transactions

import androidx.room.TypeConverter

object SpreadSheetSyncStatusTypeConverters {

    @TypeConverter
    @JvmStatic
    fun toSpreadSheetSyncStatus(ordinal: Int) = SpreadSheetSyncStatus.values()[ordinal]

    @TypeConverter
    @JvmStatic
    fun toOrdinal(spreadSheetSyncStatus: SpreadSheetSyncStatus) = spreadSheetSyncStatus.ordinal
}