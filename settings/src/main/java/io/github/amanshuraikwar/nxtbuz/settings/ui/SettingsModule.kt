package io.github.amanshuraikwar.nxtbuz.settings.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

@Module
abstract class SettingsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun a(a: SettingsViewModel): ViewModel

    @ExperimentalAnimationApi
    @Binds
    internal abstract fun b(a: SettingsActivity): AppCompatActivity
}
