package io.github.amanshuraikwar.nxtbuz.ui.starred

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@Module
internal abstract class StarredBusArrivalsModule {

    @Binds
    @IntoMap
    @ViewModelKey(StarredBusArrivalsViewModel::class)
    internal abstract fun a(a: StarredBusArrivalsViewModel): ViewModel

    @Binds
    internal abstract fun b(a: StarredBusArrivalsActivity): AppCompatActivity
}
