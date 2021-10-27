package io.github.amanshuraikwar.nxtbuz.di.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.di.LocalDataSourceParams
import io.github.amanshuraikwar.nxtbuz.di.LocalDataSourceProvides
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import javax.inject.Singleton

@Module
class LocalDataSourceProvides {
    @Singleton
    @Provides
    fun provideLocalDataSource(
        @ApplicationContext context: Context,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource {
        return LocalDataSourceProvides.provideLocalDataSource(
            localDataSourceParams = LocalDataSourceParams(context = context),
            dispatcherProvider = dispatcherProvider
        )
    }
}