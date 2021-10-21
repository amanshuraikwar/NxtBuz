package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.roomdb.RoomDbLocalDataSource

actual object LocalDataSourceProvides {
    actual fun provideRoomDbLocalDataSource(
        localDataSourceParams: LocalDataSourceParams,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource {
        return RoomDbLocalDataSource.createInstance(
            context = localDataSourceParams.context,
            ioDispatcher = dispatcherProvider.io
        )
    }
}