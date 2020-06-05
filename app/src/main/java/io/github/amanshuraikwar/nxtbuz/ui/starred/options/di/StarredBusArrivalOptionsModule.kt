package io.github.amanshuraikwar.nxtbuz.ui.starred.options.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.ui.starred.options.StarredBusArrivalOptionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@Module
internal abstract class StarredBusArrivalOptionsModule {

    @Binds
    @IntoMap
    @ViewModelKey(StarredBusArrivalOptionsViewModel::class)
    internal abstract fun a(a: StarredBusArrivalOptionsViewModel): ViewModel
}
