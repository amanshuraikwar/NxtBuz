package io.github.amanshuraikwar.howmuch.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.howmuch.di.ViewModelKey
import io.github.amanshuraikwar.howmuch.ui.onboarding.setup.SetupFragment
import io.github.amanshuraikwar.howmuch.ui.onboarding.setup.SetupViewModel
import io.github.amanshuraikwar.howmuch.ui.onboarding.welcome.WelcomeFragment

@Module
internal abstract class OnboardingModule {

    @Binds
    internal abstract fun b(a: OnboardingActivity): AppCompatActivity

    @ContributesAndroidInjector
    internal abstract fun c(): WelcomeFragment

    @Binds
    @IntoMap
    @ViewModelKey(SetupViewModel::class)
    internal abstract fun d(a: SetupViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun e(): SetupFragment
}
