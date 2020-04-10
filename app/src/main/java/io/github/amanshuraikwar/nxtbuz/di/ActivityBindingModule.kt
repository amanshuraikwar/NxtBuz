package io.github.amanshuraikwar.nxtbuz.di

import io.github.amanshuraikwar.nxtbuz.ui.launcher.LaunchModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.amanshuraikwar.nxtbuz.ui.busstop.BusStopActivity
import io.github.amanshuraikwar.nxtbuz.ui.busstop.BusStopModule
import io.github.amanshuraikwar.nxtbuz.ui.launcher.LauncherActivity
import io.github.amanshuraikwar.nxtbuz.ui.main.BackPressedProvides
import io.github.amanshuraikwar.nxtbuz.ui.main.MainActivity
import io.github.amanshuraikwar.nxtbuz.ui.main.MainModule
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.OnboardingActivity
import io.github.amanshuraikwar.nxtbuz.ui.onboarding.OnboardingModule
import io.github.amanshuraikwar.nxtbuz.ui.search.SearchActivity
import io.github.amanshuraikwar.nxtbuz.ui.search.SearchModule
import io.github.amanshuraikwar.nxtbuz.ui.settings.SettingsActivity
import io.github.amanshuraikwar.nxtbuz.ui.settings.SettingsModule
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
    @ContributesAndroidInjector(modules = [LaunchModule::class])
    internal abstract fun a(): LauncherActivity

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @ActivityScoped
    @ContributesAndroidInjector(modules = [OnboardingModule::class])
    internal abstract fun b(): OnboardingActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class, BackPressedProvides::class])
    internal abstract fun c(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [BusStopModule::class])
    internal abstract fun d(): BusStopActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SearchModule::class])
    internal abstract fun e(): SearchActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [SettingsModule::class])
    internal abstract fun f(): SettingsActivity
}
