package io.github.amanshuraikwar.nxtbuz.busstop.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class BusStopsModule {

    @Binds
    @IntoMap
    @ViewModelKey(BusStopsViewModel::class)
    abstract fun provideBusStopsViewModel(a: BusStopsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BusStopArrivalsViewModel::class)
    abstract fun provideBusStopArrivalsViewModel(a: BusStopArrivalsViewModel): ViewModel
}
