package io.github.amanshuraikwar.howmuch.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryEntity
import io.github.amanshuraikwar.howmuch.data.room.transactions.SpreadSheetSyncStatus
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionDao
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao

@Database(
    entities = [
        UserSpreadSheetEntity::class,
        CategoryEntity::class,
        TransactionEntity::class
    ],
    version = 3
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "howmuch"
    }

    abstract val transactionDao: TransactionDao

    abstract val userSpreadSheetDao: UserSpreadSheetDao

    abstract val categoryDao: CategoryDao
}

class RoomTypeConverters {

    @TypeConverter
    fun toSpreadSheetSyncStatus(ordinal: Int) = SpreadSheetSyncStatus.values()[ordinal]

    @TypeConverter
    fun toOrdinal(spreadSheetSyncStatus: SpreadSheetSyncStatus) = spreadSheetSyncStatus.ordinal
}