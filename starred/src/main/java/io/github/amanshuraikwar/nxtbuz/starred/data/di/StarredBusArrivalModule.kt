package io.github.amanshuraikwar.nxtbuz.starred.data.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.nxtbuz.starred.delegate.BusArrivalsDelegate
import io.github.amanshuraikwar.nxtbuz.starred.delegate.BusArrivalsDelegateImpl
import javax.inject.Singleton

@Module
abstract class StarredBusArrivalModule {

    @Binds
    @Singleton
    internal abstract fun a(a: io.github.amanshuraikwar.nxtbuz.starred.delegate.BusArrivalsDelegateImpl): io.github.amanshuraikwar.nxtbuz.starred.delegate.BusArrivalsDelegate
}