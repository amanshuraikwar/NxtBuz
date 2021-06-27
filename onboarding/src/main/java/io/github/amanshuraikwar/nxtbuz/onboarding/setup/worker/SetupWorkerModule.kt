package io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.SetupViewModel

@Module
interface SetupWorkerModule {
    @ContributesAndroidInjector
    fun setupWorker(): SetupWorker

    @Binds
    @IntoMap
    @ViewModelKey(SetupViewModel::class)
    fun setupViewModel(a: SetupViewModel): ViewModel
}