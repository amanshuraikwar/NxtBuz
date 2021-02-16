package io.github.amanshuraikwar.nxtbuz.busroute.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteFragment
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteViewModel
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class BusRouteModule {

    @ContributesAndroidInjector
    abstract fun busRouteFragment(): BusRouteFragment

    @Binds
    @IntoMap
    @ViewModelKey(BusRouteViewModel::class)
    abstract fun provideBusRouteViewModel(a: BusRouteViewModel): ViewModel
}
