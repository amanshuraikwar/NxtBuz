package io.github.amanshuraikwar.howmuch.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.howmuch.di.ViewModelKey
import io.github.amanshuraikwar.howmuch.ui.main.overview.OverviewFragment
import io.github.amanshuraikwar.howmuch.ui.main.overview.OverviewViewModel
import io.github.amanshuraikwar.howmuch.ui.main.profile.ProfileFragment
import io.github.amanshuraikwar.howmuch.ui.main.profile.ProfileViewModel

@Module
internal abstract class MainModule {

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    internal abstract fun a(a: ProfileViewModel): ViewModel

    @Binds
    internal abstract fun b(a: MainActivity): AppCompatActivity

    @ContributesAndroidInjector
    internal abstract fun c(): ProfileFragment

    @Binds
    @IntoMap
    @ViewModelKey(OverviewViewModel::class)
    internal abstract fun d(a: OverviewViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun e(): OverviewFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun f(a: MainViewModel): ViewModel
}
