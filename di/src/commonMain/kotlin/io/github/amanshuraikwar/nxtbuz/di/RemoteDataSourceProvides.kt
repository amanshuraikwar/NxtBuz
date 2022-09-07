package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nsapi.NsApiRemoteDataSource
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.KtorRemoteDataSource
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher

object RemoteDataSourceProvides {
    fun provideRemoteDataSource(
        ltaAccountKey: String,
        addLoggingInterceptors: Boolean,
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider
    ): RemoteDataSource {
        return KtorRemoteDataSource.createInstance(
            ltaAccountKey = ltaAccountKey,
            addLoggingInterceptors = addLoggingInterceptors,
            ioDispatcher = coroutinesDispatcherProvider.io
        )
    }

    fun provideNsApiRemoteDataSource(
        subscriptionKey: String,
        addLoggingInterceptors: Boolean,
        coroutinesDispatcherProvider: CoroutinesDispatcherProvider
    ): RemoteDataSource {
        return NsApiRemoteDataSource.createInstance(
            subscriptionKey = subscriptionKey,
            addLoggingInterceptors = addLoggingInterceptors,
            ioDispatcher = coroutinesDispatcherProvider.io,
        )
    }
}