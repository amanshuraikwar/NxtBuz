package io.github.amanshuraikwar.nxtbuz.data.room.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import io.github.amanshuraikwar.nxtbuz.data.busapi.SgBusApi
import io.github.amanshuraikwar.nxtbuz.data.room.AppDatabase
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalDao
import io.github.amanshuraikwar.nxtbuz.data.room.busoperator.BusOperatorDao
import io.github.amanshuraikwar.nxtbuz.data.room.busroute.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopsDao
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class RoomProvides {

    @Provides
    fun a(context: Context): AppDatabase {
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