package io.github.amanshuraikwar.nxtbuz.onboarding.setup.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.di.dagger.UseCaseProvides
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.SetupViewModel
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker.SetupWorker

@Module
interface SetupModule {
    @ContributesAndroidInjector(modules = [UseCaseProvides::class])
    fun setupWorker(): SetupWorker

    @Binds
    @IntoMap
    @ViewModelKey(SetupViewModel::class)
    fun setupViewModel(a: SetupViewModel): ViewModel
}