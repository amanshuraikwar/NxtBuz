package io.github.amanshuraikwar.nxtbuz.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.OverviewFragment
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.OverviewViewModel

@Module
internal abstract class MainModule {

    @Binds
    internal abstract fun a(a: MainActivity): AppCompatActivity

    @Binds
    @IntoMap
    @ViewModelKey(OverviewViewModel::class)
    internal abstract fun b(a: OverviewViewModel): ViewModel

    @ContributesAndroidInjector
    internal abstract fun c(): OverviewFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun d(a: MainViewModel): ViewModel
}
