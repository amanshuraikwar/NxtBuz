package io.github.amanshuraikwar.nxtbuz.starred.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.StarredBusArrivalOptionsDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@Module
abstract class StarredBusArrivalsModule {

    @Binds
    @IntoMap
    @ViewModelKey(StarredBusArrivalsViewModel::class)
    internal abstract fun a(a: StarredBusArrivalsViewModel): ViewModel

    @Binds
    internal abstract fun b(a: StarredBusArrivalsActivity): AppCompatActivity

    @ContributesAndroidInjector
    internal abstract fun c(): StarredBusArrivalOptionsDialogFragment

}