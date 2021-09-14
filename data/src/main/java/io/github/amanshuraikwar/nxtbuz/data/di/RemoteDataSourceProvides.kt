package io.github.amanshuraikwar.nxtbuz.data.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.ltaapi.RetrofitRemoteDataSource
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.BuildConfig
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import javax.inject.Singleton

@Module
class RemoteDataSourceProvides {
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider
    ): RemoteDataSource {
        return RetrofitRemoteDataSource.createInstance(
            addLoggingInterceptors = BuildConfig.BUILD_TYPE == "debug"
                    || BuildConfig.BUILD_TYPE == "internal",
            ioDispatcher = coroutinesDispatcherProvider.io
        )
    }
}