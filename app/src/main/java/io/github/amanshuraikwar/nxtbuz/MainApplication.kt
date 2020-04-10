package io.github.amanshuraikwar.nxtbuz

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.amanshuraikwar.nxtbuz.di.DaggerAppComponent

/**
 * Initialization of libraries.
 */
class MainApplication : DaggerApplication() {

    override fun onCreate() {

        // ThreeTenBP for times and dates, called before super to be available for objects
        AndroidThreeTen.init(this)

        // Enable strict mode before Dagger creates graph
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }

        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }
}
