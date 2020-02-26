package io.github.amanshuraikwar.howmuch.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.amanshuraikwar.howmuch.data.room.busroute.BusRouteDao
import io.github.amanshuraikwar.howmuch.data.room.busroute.BusRouteEntity
import io.github.amanshuraikwar.howmuch.data.room.busstops.BusStopEntity
import io.github.amanshuraikwar.howmuch.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryEntity
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.OperatingBusEntity
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.TimeTypeConverters
import io.github.amanshuraikwar.howmuch.data.room.transactions.SpreadSheetSyncStatusTypeConverters
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionDao
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetEntity
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao

@Database(
    entities = [
        UserSpreadSheetEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BusStopEntity::class,
        OperatingBusEntity::class,
        BusRouteEntity::class
    ],
    version = 4
)
@TypeConverters(SpreadSheetSyncStatusTypeConverters::class, TimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "buslah"
    }

    abstract val transactionDao: TransactionDao

    abstract val userSpreadSheetDao: UserSpreadSheetDao

    abstract val categoryDao: CategoryDao

    abstract val busStopDao: BusStopDao

    abstract val operatingBusDao: OperatingBusDao

    abstract val busRouteDao: BusRouteDao
}