package io.github.amanshuraikwar.nxtbuz.launcher

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey

/**
 * Module where classes needed for app launch are defined.
 */
@Module
@Suppress("UNUSED")
abstract class LauncherModule {

    /**
     * The ViewModels are created by Dagger in a map. Via the @ViewModelKey, we define that we
     * want to get a [LauncherViewModel] class.
     */
    @Binds
    @IntoMap
    @ViewModelKey(LauncherViewModel::class)
    internal abstract fun a(viewModel: LauncherViewModel): ViewModel
}
