package io.github.amanshuraikwar.howmuch.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.howmuch.di.ViewModelKey

@Module
internal abstract class OnboardingModule {

    @Binds
    @IntoMap
    @ViewModelKey(OnboardingViewModel::class)
    internal abstract fun a(viewModel: OnboardingViewModel): ViewModel

    @Binds
    internal abstract fun activity(a: OnboardingActivity): AppCompatActivity

}
