package io.github.amanshuraikwar.nxtbuz.di.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.commonkmm.AndroidSystemThemeHelper
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.di.RepositoryProvides
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.SearchRepository
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import io.github.amanshuraikwar.nxtbuz.starreddata.StarredBusArrivalRepositoryAndroidImpl
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepositoryAndroidImpl
import javax.inject.Named
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
        return UserRepositoryAndroidImpl(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
            systemThemeHelper = AndroidSystemThemeHelper(context)
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
        @Named("room") roomLocalDataSource: LocalDataSource,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): StarredBusArrivalRepository {
        return StarredBusArrivalRepositoryAndroidImpl(
            sqlDelightLocalDataSource = localDataSource,
            roomLocalDataSource = roomLocalDataSource,
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
