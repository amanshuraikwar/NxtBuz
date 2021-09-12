package io.github.amanshuraikwar.nxtbuz.data.room.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.roomdb.AppDatabase
import io.github.amanshuraikwar.nxtbuz.roomdb.RoomDbLocalDataSource
import javax.inject.Singleton

@Module
class RoomProvides {
//    @Singleton
//    @Provides
//    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
//        return AppDatabase.getInstance(context)
//    }

    @Singleton
    @Provides
    fun provideRoomDbLocalDataSource(
        @ApplicationContext context: Context,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource {
        return RoomDbLocalDataSource(
            appDatabase = AppDatabase.getInstance(context),
            ioDispatcher = dispatcherProvider.io
        )
    }
}