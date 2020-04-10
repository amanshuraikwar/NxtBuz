package io.github.amanshuraikwar.nxtbuz.ui.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey

@Module
internal abstract class SettingsModule {

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun a(a: SettingsViewModel): ViewModel

    @Binds
    internal abstract fun b(a: SettingsActivity): AppCompatActivity
}
