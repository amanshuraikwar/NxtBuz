package io.github.amanshuraikwar.howmuch.data.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.howmuch.data.busapi.SgBusApi
import io.github.amanshuraikwar.howmuch.data.room.AppDatabase
import io.github.amanshuraikwar.howmuch.data.room.busroute.BusRouteDao
import io.github.amanshuraikwar.howmuch.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.operatingbus.OperatingBusDao
import io.github.amanshuraikwar.howmuch.data.room.transactions.TransactionDao
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class DataModuleProvides {

    @Provides
    fun a(): HttpTransport = AndroidHttp.newCompatibleTransport()

    @Provides
    fun b(): JsonFactory = JacksonFactory.getDefaultInstance()

    @Provides
    fun c(context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun d(appDatabase: AppDatabase): UserSpreadSheetDao {
        return appDatabase.userSpreadSheetDao
    }

    @Provides
    fun e(appDatabase: AppDatabase): CategoryDao {
        return appDatabase.categoryDao
    }

    @Provides
    fun f(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao
    }

    @Provides
    fun g(): SgBusApi {
        return Retrofit
            .Builder()
            .baseUrl("http://datamall2.mytransport.sg/ltaodataservice/")
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()
                        var string: String = request.url.toString()
                        string = string.replace("%60%24%60", "$")
                        val newRequest = Request
                            .Builder()
                            .addHeader("AccountKey", "yO8B1RhDRoesLHDACerOUg==")
                            .url(string)
                            .build()
                        chain.proceed(newRequest)
                    }
                    .addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SgBusApi::class.java)
    }

    @Provides
    fun h(appDatabase: AppDatabase): BusStopDao {
        return appDatabase.busStopDao
    }

    @Provides
    fun i(appDatabase: AppDatabase): BusRouteDao {
        return appDatabase.busRouteDao
    }

    @Provides
    fun j(appDatabase: AppDatabase): OperatingBusDao {
        return appDatabase.operatingBusDao
    }

    @Provides
    fun k(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}