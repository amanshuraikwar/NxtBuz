package io.github.amanshuraikwar.nxtbuz.di

import android.content.Context
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import io.github.amanshuraikwar.nxtbuz.MainApplication
import io.github.amanshuraikwar.nxtbuz.common.di.ApplicationContext
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * Defines all the classes that need to be provided in the scope of the app.
 *
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(application: MainApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    @Named("appVersionInfo")
    fun provideAppVersionInfo(): String {
        return if (BuildConfig.DEBUG) {
            "${BuildConfig.VERSION_NAME}(" +
                    "" +
                    "${BuildConfig.VERSION_CODE}" +
                    " • " +
                    BuildConfig.BUILD_TYPE.uppercase(Locale.getDefault()) +
                    ")"
        } else {
            "${BuildConfig.VERSION_NAME}"
        }
    }
}
