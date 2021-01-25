package io.github.amanshuraikwar.nxtbuz.map.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMapFragment
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMapViewModel

@Module
abstract class MapModule {

    @ContributesAndroidInjector
    abstract fun nxtBuzMapFragment(): NxtBuzMapFragment

    @Binds
    @IntoMap
    @ViewModelKey(NxtBuzMapViewModel::class)
    abstract fun provideMapViewModel(a: NxtBuzMapViewModel): ViewModel
}
