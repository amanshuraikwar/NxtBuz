package io.github.amanshuraikwar.nxtbuz.data.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.DispatcherProviderFactory
import javax.inject.Singleton

@Module
class CoroutineProvides {
    @Singleton
    @Provides
    fun provideDispatcherProvider(): CoroutinesDispatcherProvider {
        return DispatcherProviderFactory.getDispatcherProvider()
    }
}