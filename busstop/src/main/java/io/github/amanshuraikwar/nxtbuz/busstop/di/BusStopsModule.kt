package io.github.amanshuraikwar.nxtbuz.busstop.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsFragment
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class BusStopsModule {

    @ContributesAndroidInjector
    abstract fun busStopsFragment(): BusStopsFragment

    @Binds
    @IntoMap
    @ViewModelKey(BusStopsViewModel::class)
    abstract fun provideBusStopsViewModel(a: BusStopsViewModel): ViewModel
}
