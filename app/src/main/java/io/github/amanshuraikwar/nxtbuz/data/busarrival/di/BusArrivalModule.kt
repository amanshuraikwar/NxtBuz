package io.github.amanshuraikwar.nxtbuz.data.busarrival.di

import dagger.Binds
import dagger.Module
import io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates.BusArrivalStateFlowDelegate
import io.github.amanshuraikwar.nxtbuz.data.busarrival.delegates.BusArrivalStateFlowDelegateImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
abstract class BusArrivalModule {

    @Binds
    @Singleton
    internal abstract fun a(a: BusArrivalStateFlowDelegateImpl): BusArrivalStateFlowDelegate
}