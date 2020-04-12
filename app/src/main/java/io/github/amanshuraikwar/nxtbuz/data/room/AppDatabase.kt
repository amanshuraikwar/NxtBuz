package io.github.amanshuraikwar.nxtbuz.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.amanshuraikwar.nxtbuz.data.room.busroute.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.busroute.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.TimeTypeConverters
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopsDao

@Database(
    entities = [
        BusStopEntity::class,
        OperatingBusEntity::class,
        BusRouteEntity::class,
        StarredBusStopEntity::class
    ],
    version = 6
)
@TypeConverters(TimeTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "nxtbuz"
    }

    abstract val busStopDao: BusStopDao

    abstract val operatingBusDao: OperatingBusDao

    abstract val busRouteDao: BusRouteDao

    abstract val starredBusStopsDao: StarredBusStopsDao
}