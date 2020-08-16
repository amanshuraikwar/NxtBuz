package io.github.amanshuraikwar.nxtbuz.onboarding

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.onboarding.permission.PermissionFragment
import io.github.amanshuraikwar.nxtbuz.onboarding.permission.PermissionViewModel
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.SetupFragment
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.SetupViewModel
import io.github.amanshuraikwar.nxtbuz.onboarding.welcome.WelcomeFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Module
abstract class OnboardingModule {

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
