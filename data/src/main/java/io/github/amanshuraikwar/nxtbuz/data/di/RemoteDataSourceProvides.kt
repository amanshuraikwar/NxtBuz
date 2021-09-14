package io.github.amanshuraikwar.nxtbuz.data.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.BuildConfig
import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.KtorRemoteDataSource
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import javax.inject.Named
import javax.inject.Singleton

@Module
class RemoteDataSourceProvides {
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        @Named("ltaAccountKey") ltaAccountKey: String,
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider
    ): RemoteDataSource {
        return KtorRemoteDataSource.createInstance(
            ltaAccountKey = ltaAccountKey,
            addLoggingInterceptors = BuildConfig.BUILD_TYPE == "debug"
                    || BuildConfig.BUILD_TYPE == "internal",
            ioDispatcher = coroutinesDispatcherProvider.io
        )
    }
}