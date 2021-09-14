package io.github.amanshuraikwar.nxtbuz.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.amanshuraikwar.nxtbuz.MainApplication
import io.github.amanshuraikwar.nxtbuz.data.di.RemoteDataSourceProvides
import io.github.amanshuraikwar.nxtbuz.data.di.PreferenceProvides
import io.github.amanshuraikwar.nxtbuz.data.di.LocalDataSourceProvides
import io.github.amanshuraikwar.nxtbuz.data.location.di.LocationModuleProvides
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.map.di.MapProvides
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.di.SetupModule
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
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
        ViewModelModule::class,
        RemoteDataSourceProvides::class,
        LocationModuleProvides::class,
        PreferenceProvides::class,
        LocalDataSourceProvides::class,
        MapProvides::class,
        SetupModule::class,
    ]
)
interface AppComponent : AndroidInjector<MainApplication> {
    fun getLocalDataSource(): LocalDataSource
    fun getRemoteDataSource(): RemoteDataSource
    fun getPreferenceStorage(): PreferenceStorage

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: MainApplication): AppComponent
    }
}
