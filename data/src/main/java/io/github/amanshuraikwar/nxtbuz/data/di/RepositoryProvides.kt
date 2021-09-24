package io.github.amanshuraikwar.nxtbuz.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.busarrivaldata.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.busroutedata.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.starreddata.StarredBusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.userdata.SystemThemeHelper
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository
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
        return BusStopRepository(
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
        return UserRepository(
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
        return BusRouteRepository(
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
        return BusArrivalRepository(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Singleton
    @Provides
    fun provideStarredBusArrivalRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): StarredBusArrivalRepository {
        return StarredBusArrivalRepository(
            localDataSource = localDataSource,
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }
}