package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nsapi.provider.NsApiFactory
import io.github.amanshuraikwar.nxtbuz.busarrivaldata.BusArrivalRepositoryImpl
import io.github.amanshuraikwar.nxtbuz.busroutedata.BusRouteRepositoryImpl
import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepositoryImpl
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.SystemThemeHelper
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.SearchRepository
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import io.github.amanshuraikwar.nxtbuz.searchdata.SearchRepositoryImpl
import io.github.amanshuraikwar.nxtbuz.starreddata.StarredBusArrivalRepositoryImpl
import io.github.amanshuraikwar.nxtbuz.userdata.UserRepositoryImpl

object RepositoryProvides {
    fun provideBusStopRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): BusStopRepository {
        return BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    fun provideUserRepository(
        systemThemeHelper: SystemThemeHelper,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): UserRepository {
        return UserRepositoryImpl(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
            systemThemeHelper = systemThemeHelper
        )
    }

    fun provideBusRouteRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): BusRouteRepository {
        return BusRouteRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }

    fun provideBusArrivalRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): BusArrivalRepository {
        return BusArrivalRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }

    fun provideStarredBusArrivalRepository(
        localDataSource: LocalDataSource,
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): StarredBusArrivalRepository {
        return StarredBusArrivalRepositoryImpl(
            localDataSource = localDataSource,
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    fun provideSearchRepository(
        localDataSource: LocalDataSource,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): SearchRepository {
        return SearchRepositoryImpl(
            localDataSource = localDataSource,
            dispatcherProvider = dispatcherProvider,
        )
    }

    fun provideNsApiTrainStopRepository(
        nsApiFactory: NsApiFactory
    ): TrainStopRepository {
        return nsApiFactory.create()
    }
}
