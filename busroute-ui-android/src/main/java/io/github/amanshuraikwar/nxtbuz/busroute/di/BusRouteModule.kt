package io.github.amanshuraikwar.nxtbuz.busroute.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteViewModel
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class BusRouteModule {

    @Binds
    @IntoMap
    @ViewModelKey(BusRouteViewModel::class)
    abstract fun provideBusRouteViewModel(a: BusRouteViewModel): ViewModel
}
