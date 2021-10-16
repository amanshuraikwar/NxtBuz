package io.github.amanshuraikwar.nxtbuz.map.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMapViewModel
import io.github.amanshuraikwar.nxtbuz.map.ui.recenter.RecenterViewModel

@Module
abstract class MapModule {
    @Binds
    @IntoMap
    @ViewModelKey(NxtBuzMapViewModel::class)
    abstract fun provideMapViewModel(a: NxtBuzMapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecenterViewModel::class)
    abstract fun provideRecenterViewModel(a: RecenterViewModel): ViewModel
}
