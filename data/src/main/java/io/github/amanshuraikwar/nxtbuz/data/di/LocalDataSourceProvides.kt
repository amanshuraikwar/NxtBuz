package io.github.amanshuraikwar.nxtbuz.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.sqldelightdb.DbFactory
import io.github.amanshuraikwar.nxtbuz.sqldelightdb.SqlDelightLocalDataSource
import javax.inject.Singleton

@Module
class LocalDataSourceProvides {
    @Singleton
    @Provides
    fun provideRoomDbLocalDataSource(
        @ApplicationContext context: Context,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource {
        return SqlDelightLocalDataSource.createInstance(
            dbFactory = DbFactory(context),
            ioDispatcher = dispatcherProvider.io
        )
    }
}