package io.github.amanshuraikwar.howmuch.data.di

import android.content.Context
import androidx.room.Room
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.howmuch.data.room.AppDatabase
import io.github.amanshuraikwar.howmuch.data.room.categories.CategoryDao
import io.github.amanshuraikwar.howmuch.data.room.userspreadsheet.UserSpreadSheetDao

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
}