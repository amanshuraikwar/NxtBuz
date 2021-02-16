package io.github.amanshuraikwar.nxtbuz.di

import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import io.github.amanshuraikwar.nxtbuz.MainApplication
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.busroute.BusRouteNavigationParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    fun provideContext(application: MainApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesWifiManager(context: Context): WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    @Singleton
    fun providesConnectivityManager(context: Context): ConnectivityManager =
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    @Provides
    @Singleton
    fun providesClipboardManager(context: Context): ClipboardManager =
        context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager

    @Provides
    @Singleton
    @Named("appVersionInfo")
    fun provideAppVersionInfo(): String {
        return if (BuildConfig.DEBUG) {
            "V${BuildConfig.VERSION_NAME}" +
                    " <<" +
                    "${BuildConfig.VERSION_CODE}" +
                    " • " +
                    BuildConfig.BUILD_TYPE.toUpperCase(Locale.getDefault()) +
                    ">>"
        } else {
            "V${BuildConfig.VERSION_NAME}"
        }
    }

    @Provides
    @Singleton
    @Named("bottomSheetSlideOffset")
    fun provideBottomSheetSlideOffsetFlow(): MutableStateFlow<Float> {
        return MutableStateFlow(0f)
    }

    @Provides
    @Singleton
    @Named("navigateToBusStopArrivals")
    fun provideNavigateToBusStopArrivals(): MutableSharedFlow<BusStop> {
        return MutableSharedFlow(replay = 0)
    }

    @Provides
    @Singleton
    @Named("navigateToBusRoute")
    fun provideNavigateToBusRoute(): MutableSharedFlow<BusRouteNavigationParams> {
        return MutableSharedFlow(replay = 0)
    }
}
