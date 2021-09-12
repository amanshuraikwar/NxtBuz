package io.github.amanshuraikwar.nxtbuz.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.roomdb.RoomDbLocalDataSource
import javax.inject.Singleton

@Module
class RoomProvides {
    @Singleton
    @Provides
    fun provideRoomDbLocalDataSource(
        @ApplicationContext context: Context,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource {
        return RoomDbLocalDataSource.createInstance(
            context = context,
            ioDispatcher = dispatcherProvider.io
        )
    }
}