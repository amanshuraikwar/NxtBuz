package io.github.amanshuraikwar.nxtbuz.data.room.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.data.room.AppDatabase
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusArrivalDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusOperatorDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao

@Module
class RoomProvides {

    @Provides
    fun a(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .addMigrations(AppDatabase.MIGRATION_6_7)
            .build()
    }

    @Provides
    fun b(appDatabase: AppDatabase): BusStopDao {
        return appDatabase.busStopDao
    }

    @Provides
    fun c(appDatabase: AppDatabase): BusRouteDao {
        return appDatabase.busRouteDao
    }

    @Provides
    fun d(appDatabase: AppDatabase): OperatingBusDao {
        return appDatabase.operatingBusDao
    }

    @Provides
    fun f(appDatabase: AppDatabase): StarredBusStopsDao {
        return appDatabase.starredBusStopsDao
    }

    @Provides
    fun g(appDatabase: AppDatabase): BusOperatorDao {
        return appDatabase.busOperatorDao
    }

    @Provides
    fun h(appDatabase: AppDatabase): BusArrivalDao {
        return appDatabase.busArrivalDao
    }
}