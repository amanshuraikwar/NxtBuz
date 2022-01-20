package io.github.amanshuraikwar.nxtbuz.di

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.amanshuraikwar.nxtbuz.busroute.di.BusRouteModule
import io.github.amanshuraikwar.nxtbuz.busstop.di.BusStopsModule
import io.github.amanshuraikwar.nxtbuz.common.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtilProvides
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtilProvides
import io.github.amanshuraikwar.nxtbuz.di.dagger.UseCaseProvides
import io.github.amanshuraikwar.nxtbuz.map.di.MapModule
import io.github.amanshuraikwar.nxtbuz.search.SearchModule
import io.github.amanshuraikwar.nxtbuz.settings.ui.SettingsActivity
import io.github.amanshuraikwar.nxtbuz.settings.ui.SettingsModule
import io.github.amanshuraikwar.nxtbuz.starred.StarredModule
import io.github.amanshuraikwar.nxtbuz.ui.MainActivity
import io.github.amanshuraikwar.nxtbuz.ui.di.MainModule

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@ExperimentalAnimationApi
@ExperimentalAnimatedInsets
@ExperimentalMaterialApi
@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {
    @ActivityScoped
    @ContributesAndroidInjector(
        modules = [
            MainModule::class,
            PermissionUtilProvides::class,
            LocationUtilProvides::class,
            MapModule::class,
            BusStopsModule::class,
            BusRouteModule::class,
            SearchModule::class,
            StarredModule::class,
            CoroutineModule::class,
            UseCaseProvides::class
        ]
    )
    internal abstract fun c(): MainActivity

    @ExperimentalAnimationApi
    @ActivityScoped
    @ContributesAndroidInjector(modules = [SettingsModule::class, UseCaseProvides::class])
    internal abstract fun e(): SettingsActivity
}
