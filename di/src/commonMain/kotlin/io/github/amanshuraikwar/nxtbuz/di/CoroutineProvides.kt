package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.DispatcherProviderFactory

object CoroutineProvides {
    fun provideDispatcherProvider(): CoroutinesDispatcherProvider {
        return DispatcherProviderFactory.getDispatcherProvider()
    }
}