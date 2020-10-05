package io.github.amanshuraikwar.nxtbuz.starred.data.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.nxtbuz.starred.data.delegate.BusArrivalsDelegate
import io.github.amanshuraikwar.nxtbuz.starred.data.delegate.BusArrivalsDelegateImpl
import javax.inject.Singleton

@Module
abstract class StarredBusArrivalModule {

    @Binds
    @Singleton
    abstract fun a(a: BusArrivalsDelegateImpl): BusArrivalsDelegate
}