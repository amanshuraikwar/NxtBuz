package io.github.amanshuraikwar.nxtbuz.ui.onboarding

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.permission.PermissionFragment
import io.github.amanshuraikwar.nxtbuz.ui.permission.PermissionViewModel
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.setup.SetupFragment
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.setup.SetupViewModel
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.welcome.WelcomeFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
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

    @Binds
    @IntoMap
    @ViewModelKey(PermissionViewModel::class)
    internal abstract fun f(a: PermissionViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun g(): PermissionFragment
}
