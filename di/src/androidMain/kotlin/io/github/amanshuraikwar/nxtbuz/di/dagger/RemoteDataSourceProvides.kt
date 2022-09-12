package io.github.amanshuraikwar.nxtbuz.di.dagger

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.di.RemoteDataSourceProvides
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import javax.inject.Named
import javax.inject.Singleton

@Module
class RemoteDataSourceProvides {
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        @Named("ltaAccountKey") ltaAccountKey: String,
        @Named("isReleaseBuild") isReleaseBuild: Boolean,
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider
    ): RemoteDataSource {
        return RemoteDataSourceProvides.provideRemoteDataSource(
            ltaAccountKey = ltaAccountKey,
            addLoggingInterceptors = !isReleaseBuild,
            coroutinesDispatcherProvider = coroutinesDispatcherProvider
        )
    }
}