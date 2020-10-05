package io.github.amanshuraikwar.nxtbuz.starred.ui.options.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.StarredBusArrivalOptionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@Module
abstract class StarredBusArrivalOptionsModule {

    @Binds
    @IntoMap
    @ViewModelKey(StarredBusArrivalOptionsViewModel::class)
    internal abstract fun a(a: StarredBusArrivalOptionsViewModel): ViewModel
}
