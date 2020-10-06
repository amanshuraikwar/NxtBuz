package io.github.amanshuraikwar.nxtbuz.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtilProvides
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtilProvides
import io.github.amanshuraikwar.nxtbuz.launcher.LauncherActivity
import io.github.amanshuraikwar.nxtbuz.launcher.LauncherModule
import io.github.amanshuraikwar.nxtbuz.onboarding.OnboardingActivity
import io.github.amanshuraikwar.nxtbuz.onboarding.OnboardingModule
import io.github.amanshuraikwar.nxtbuz.search.SearchActivity
import io.github.amanshuraikwar.nxtbuz.search.SearchModule
import io.github.amanshuraikwar.nxtbuz.ui.main.di.MainLiveDataProvides
import io.github.amanshuraikwar.nxtbuz.ui.main.MainActivity
import io.github.amanshuraikwar.nxtbuz.ui.main.di.MainModule
import io.github.amanshuraikwar.nxtbuz.settings.ui.SettingsActivity
import io.github.amanshuraikwar.nxtbuz.settings.ui.SettingsModule
import io.github.amanshuraikwar.nxtbuz.starred.ui.StarredBusArrivalsActivity
import io.github.amanshuraikwar.nxtbuz.starred.ui.StarredBusArrivalsModule
import io.github.amanshuraikwar.nxtbuz.starred.ui.di.StarredBusArrivalsProvides
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.di.StarredBusArrivalOptionsModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LauncherModule::class])
    internal abstract fun a(): LauncherActivity

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        OnboardingModule::class,
        PermissionUtilProvides::class,
        LocationUtilProvides::class
    ])
    internal abstract fun b(): OnboardingActivity

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        MainModule::class,
        MainLiveDataProvides::class,
        PermissionUtilProvides::class,
        LocationUtilProvides::class,
        StarredBusArrivalsProvides::class,
        StarredBusArrivalOptionsModule::class
    ])
    internal abstract fun c(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SearchModule::class])
    internal abstract fun d(): SearchActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SettingsModule::class])
    internal abstract fun e(): SettingsActivity

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @ActivityScoped
    @ContributesAndroidInjector(modules = [StarredBusArrivalsModule::class, StarredBusArrivalOptionsModule::class, StarredBusArrivalsProvides::class])
    internal abstract fun f(): StarredBusArrivalsActivity
}
