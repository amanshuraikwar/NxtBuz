package io.github.amanshuraikwar.nxtbuz.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.amanshuraikwar.ltaapi.di.BusApiProvides
import io.github.amanshuraikwar.nxtbuz.MainApplication
import io.github.amanshuraikwar.nxtbuz.busstop.di.BusStopsProvides
import io.github.amanshuraikwar.nxtbuz.data.busarrival.di.BusArrivalModule
import io.github.amanshuraikwar.nxtbuz.data.busarrival.di.BusArrivalProvides
import io.github.amanshuraikwar.nxtbuz.data.location.di.LocationModuleProvides
import io.github.amanshuraikwar.nxtbuz.data.prefs.di.PrefsModuleBinds
import io.github.amanshuraikwar.nxtbuz.data.room.di.RoomProvides
import io.github.amanshuraikwar.nxtbuz.data.starred.di.StarredBusArrivalModule
import io.github.amanshuraikwar.nxtbuz.data.starred.di.StarredBusArrivalProvides
import io.github.amanshuraikwar.nxtbuz.map.di.MapProvides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

/**
 * Main component of the app, created and persisted in the Application class.
 *
 * Whenever a new module is created, it should be added to the list of modules.
 * [AndroidSupportInjectionModule] is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 */
@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBindingModule::class,
        ServiceBindingModule::class,
        ViewModelModule::class,
        BusApiProvides::class,
        LocationModuleProvides::class,
        PrefsModuleBinds::class,
        RoomProvides::class,
        BusArrivalProvides::class,
        BusArrivalModule::class,
        StarredBusArrivalModule::class,
        StarredBusArrivalProvides::class,
        MapProvides::class,
        BusStopsProvides::class
    ]
)
interface AppComponent : AndroidInjector<MainApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: MainApplication): AppComponent
    }
}
