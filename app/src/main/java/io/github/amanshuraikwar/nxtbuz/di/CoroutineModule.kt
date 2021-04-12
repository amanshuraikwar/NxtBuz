package io.github.amanshuraikwar.nxtbuz.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.plus
import javax.inject.Named

@Module
class CoroutineModule {

    @Provides
    @ActivityScoped
    @Named("mapScope")
    fun provideMapScope(
        dispatcherProvider: CoroutinesDispatcherProvider
    ): CoroutineScope {
        return MainScope() + dispatcherProvider.map + CoroutineName("mapScopeCoroutine")
    }
}