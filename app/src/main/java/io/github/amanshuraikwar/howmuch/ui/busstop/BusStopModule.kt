package io.github.amanshuraikwar.howmuch.ui.busstop

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
internal abstract class BusStopModule {

    @Binds
    @IntoMap
    @ViewModelKey(BusStopViewModel::class)
    internal abstract fun a(a: BusStopViewModel): ViewModel

    @Binds
    internal abstract fun b(a: BusStopActivity): AppCompatActivity
}
