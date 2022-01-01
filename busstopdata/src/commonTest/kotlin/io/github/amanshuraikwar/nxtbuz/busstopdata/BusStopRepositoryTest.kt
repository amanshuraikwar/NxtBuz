package io.github.amanshuraikwar.nxtbuz.busstopdata

import io.github.amanshuraikwar.testutil.FakeCoroutinesDispatcherProvider
import io.github.amanshuraikwar.testutil.FakeLocalDataSource
import io.github.amanshuraikwar.testutil.FakePreferenceStorage
import kotlin.test.Test
import kotlin.test.assertTrue

class BusStopRepositoryTest {

    @Test
    fun `setup populates the correct bus stop data in local storage`() {
        val localDataSource = FakeLocalDataSource()
        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = ,
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )
    }
}