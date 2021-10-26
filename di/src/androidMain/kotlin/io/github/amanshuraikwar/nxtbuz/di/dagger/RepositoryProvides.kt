package io.github.amanshuraikwar.nxtbuz.di.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.SystemThemeHelper
import io.github.amanshuraikwar.nxtbuz.di.RepositoryProvides
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.repository.*
import javax.inject.Singleton

@Module
class RepositoryProvides {
    @Singleton
    @Provides
    fun provideBusStopRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): BusStopRepository {
        return RepositoryProvides.provideBusStopRepository(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        @ApplicationContext context: Context,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): UserRepository {
        return RepositoryProvides.provideUserRepository(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
            systemThemeHelper = SystemThemeHelper(context)
        )
    }

    @Singleton
    @Provides
    fun provideBusRouteRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): BusRouteRepository {
        return RepositoryProvides.provideBusRouteRepository(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Singleton
    @Provides
    fun provideBusArrivalRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): BusArrivalRepository {
        return RepositoryProvides.provideBusArrivalRepository(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Singleton
    @Provides
    fun provideStarredBusArrivalRepository(
        localDataSource: LocalDataSource,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): StarredBusArrivalRepository {
        return RepositoryProvides.provideStarredBusArrivalRepository(
            localDataSource = localDataSource,
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Singleton
    @Provides
    fun provideSearchRepository(
        localDataSource: LocalDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): SearchRepository {
        return RepositoryProvides.provideSearchRepository(
            localDataSource = localDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }
}
