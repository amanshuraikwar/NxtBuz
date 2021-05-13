package io.github.amanshuraikwar.nxtbuz.data.busarrival.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates.BusArrivalStateFlowDelegate
import io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates.BusArrivalStateFlowDelegateImpl
import javax.inject.Singleton

@Module
abstract class BusArrivalModule {

    @Binds
    @Singleton
    internal abstract fun a(a: BusArrivalStateFlowDelegateImpl): BusArrivalStateFlowDelegate
}