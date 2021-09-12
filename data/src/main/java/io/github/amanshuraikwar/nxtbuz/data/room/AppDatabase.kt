//package io.github.amanshuraikwar.nxtbuz.data.room
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import androidx.room.migration.Migration
//import androidx.sqlite.db.SupportSQLiteDatabase
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusArrivalRoomDbEntity
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusOperatorEntity
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusRouteEntity
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.BusStopRoomDbEntity
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.OperatingBusEntity
//import io.github.amanshuraikwar.nxtbuz.roomdb.model.StarredBusStopEntity
//import io.github.amanshuraikwar.nxtbuz.data.room.converter.BusArrivalTypeConverters
//import io.github.amanshuraikwar.nxtbuz.data.room.converter.DateTimeTypeConverters
//import io.github.amanshuraikwar.nxtbuz.data.room.dao.*
//
//@Database(
//    entities = [
//        BusStopRoomDbEntity::class,
//        OperatingBusEntity::class,
//        BusRouteEntity::class,
//        StarredBusStopEntity::class,
//        BusOperatorEntity::class,
//        BusArrivalRoomDbEntity::class
//    ],
//    version = 7,
//    exportSchema = true
//)
//@TypeConverters(DateTimeTypeConverters::class, BusArrivalTypeConverters::class)
//abstract class AppDatabase : RoomDatabase() {
//    abstract val busStopDao: BusStopDao
//
//    abstract val operatingBusDao: OperatingBusDao
//
//    abstract val busRouteDao: BusRouteDao
//
//    abstract val starredBusStopsDao: StarredBusStopsDao
//
//    abstract val busOperatorDao: BusOperatorDao
//
//    abstract val busArrivalDao: BusArrivalDao
//
//    companion object {
//        const val DATABASE_NAME = "nxtbuz"
//
//        val MIGRATION_6_7 = object : Migration(6, 7) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE IF NOT EXISTS `BusArrivalEntity` (`busServiceNumber` TEXT NOT NULL, `busStopCode` TEXT NOT NULL, `seqNumber` INTEGER NOT NULL, `busArrivalStatus` TEXT NOT NULL, `originCode` TEXT NOT NULL, `destinationCode` TEXT NOT NULL, `estimatedArrivalTimestamp` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `visitNumber` INTEGER NOT NULL, `load` TEXT NOT NULL, `feature` TEXT NOT NULL, `type` TEXT NOT NULL, `lastUpdatedOn` TEXT NOT NULL, PRIMARY KEY(`busServiceNumber`, `busStopCode`, `seqNumber`))")
//                database.execSQL("CREATE TABLE IF NOT EXISTS `BusOperatorEntity` (`busServiceNumber` TEXT NOT NULL, `busStopCode` TEXT NOT NULL, `operator` TEXT NOT NULL, `lastUpdatedOn` TEXT NOT NULL, PRIMARY KEY(`busServiceNumber`, `busStopCode`))")
//            }
//        }
//
//        fun getInstance(context: Context): AppDatabase {
//            return Room
//                .databaseBuilder(
//                    context,
//                    AppDatabase::class.java,
//                    AppDatabase.DATABASE_NAME
//                )
//                .addMigrations(AppDatabase.MIGRATION_6_7)
//                .build()
//        }
//    }
//}