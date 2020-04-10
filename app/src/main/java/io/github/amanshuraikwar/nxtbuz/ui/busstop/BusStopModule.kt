package io.github.amanshuraikwar.nxtbuz.ui.busstop

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey

@Module
internal abstract class BusStopModule {

    @Binds
    @IntoMap
    @ViewModelKey(BusStopViewModel::class)
    internal abstract fun a(a: BusStopViewModel): ViewModel

    @Binds
    internal abstract fun b(a: BusStopActivity): AppCompatActivity
}
