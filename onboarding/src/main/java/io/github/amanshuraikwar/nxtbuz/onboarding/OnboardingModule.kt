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

@Suppress("unused")
@Module
abstract class OnboardingModule {

    @Binds
    internal abstract fun a(a: OnboardingActivity): AppCompatActivity

    @ContributesAndroidInjector
    internal abstract fun b(): WelcomeFragment

    @Binds
    @IntoMap
    @ViewModelKey(SetupViewModel::class)
    internal abstract fun c(a: SetupViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun d(): SetupFragment

    @Binds
    @IntoMap
    @ViewModelKey(PermissionViewModel::class)
    internal abstract fun e(a: PermissionViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun f(): PermissionFragment
}
