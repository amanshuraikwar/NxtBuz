package io.github.amanshuraikwar.nxtbuz.ui.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.common.di.ViewModelKey
import io.github.amanshuraikwar.nxtbuz.onboarding.permission.PermissionViewModel
import io.github.amanshuraikwar.nxtbuz.ui.MainActivity
import io.github.amanshuraikwar.nxtbuz.ui.MainViewModel
import io.github.amanshuraikwar.nxtbuz.ui.fragment.MainFragment
import io.github.amanshuraikwar.nxtbuz.ui.fragment.MainFragmentViewModel
import io.github.amanshuraikwar.nxtbuz.starred.ui.delegate.StarredArrivalsViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.starred.ui.delegate.StarredArrivalsViewModelDelegateImpl
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.StarredBusArrivalOptionsDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@Module
internal abstract class MainModule {

    @Binds
    internal abstract fun a(a: MainActivity): AppCompatActivity

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun d(a: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PermissionViewModel::class)
    internal abstract fun e(a: PermissionViewModel): ViewModel

    @ExperimentalCoroutinesApi
    @FlowPreview
    @InternalCoroutinesApi
    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModel::class)
    internal abstract fun f(a: MainFragmentViewModel): ViewModel

    @FlowPreview
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @ContributesAndroidInjector
    internal abstract fun g(): MainFragment

//    @Binds
//    @ActivityScoped
//    internal abstract fun h(a: io.github.amanshuraikwar.nxtbuz.map.MapViewModelDelegateImpl): io.github.amanshuraikwar.nxtbuz.map.MapViewModelDelegate

    @ExperimentalCoroutinesApi
    @Binds
    @ActivityScoped
    internal abstract fun i(a: StarredArrivalsViewModelDelegateImpl): StarredArrivalsViewModelDelegate

    @ContributesAndroidInjector
    internal abstract fun j(): StarredBusArrivalOptionsDialogFragment
}
