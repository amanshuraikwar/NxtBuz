package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource

expect object LocalDataSourceProvides {
    fun provideRoomDbLocalDataSource(
        localDataSourceParams: LocalDataSourceParams,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource
}