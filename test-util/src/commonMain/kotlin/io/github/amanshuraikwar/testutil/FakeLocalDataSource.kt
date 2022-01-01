package io.github.amanshuraikwar.testutil

import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.sqldelightdb.SqlDelightLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher

class FakeLocalDataSource(
    ioDispatcher: CoroutineDispatcher = FakeCoroutinesDispatcherProvider.io,
    localDataSource: LocalDataSource = SqlDelightLocalDataSource.createInstance(
        getSqlDriver(),
        ioDispatcher
    )
) : LocalDataSource by localDataSource