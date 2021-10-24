package io.github.amanshuraikwar.nxtbuz.di.dagger

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.di.CoroutineProvides
import javax.inject.Singleton

@Module
class CoroutineProvides {
    @Singleton
    @Provides
    fun provideDispatcherProvider(): CoroutinesDispatcherProvider {
        return CoroutineProvides.provideDispatcherProvider()
    }
}