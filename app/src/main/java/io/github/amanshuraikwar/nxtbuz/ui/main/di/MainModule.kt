package io.github.amanshuraikwar.nxtbuz.ui.main.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.ui.main.MainActivity
import io.github.amanshuraikwar.nxtbuz.ui.main.MainViewModel
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.MapViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.MapViewModelDelegateImpl
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.MainFragment
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.MainFragmentViewModel
import io.github.amanshuraikwar.nxtbuz.ui.permission.PermissionViewModel
import kotlinx.coroutines.InternalCoroutinesApi

@Module
internal abstract class MainModule {

    @Binds
    internal abstract fun a(a: MainActivity): AppCompatActivity

//    @Binds
//    @IntoMap
//    @ViewModelKey(OverviewViewModel::class)
//    internal abstract fun b(a: OverviewViewModel): ViewModel

//    @ContributesAndroidInjector
//    internal abstract fun c(): OverviewFragment

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun d(a: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PermissionViewModel::class)
    internal abstract fun e(a: PermissionViewModel): ViewModel

    @InternalCoroutinesApi
    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModel::class)
    internal abstract fun f(a: MainFragmentViewModel): ViewModel

    @InternalCoroutinesApi
    @ContributesAndroidInjector
    internal abstract fun g(): MainFragment

    @Binds
    @ActivityScoped
    internal abstract fun h(a: MapViewModelDelegateImpl): MapViewModelDelegate
}
