package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.sqldelightdb.DbFactory
import io.github.amanshuraikwar.nxtbuz.sqldelightdb.SqlDelightLocalDataSource

actual object LocalDataSourceProvides {
    actual fun provideLocalDataSource(
        localDataSourceParams: LocalDataSourceParams,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocalDataSource {
        return SqlDelightLocalDataSource.createInstance(
            dbFactory = DbFactory(localDataSourceParams.dbBasePathName),
            ioDispatcher = dispatcherProvider.io
        )
    }
}