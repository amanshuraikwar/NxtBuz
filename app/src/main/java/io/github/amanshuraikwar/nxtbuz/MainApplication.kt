package io.github.amanshuraikwar.nxtbuz

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.amanshuraikwar.nxtbuz.di.DaggerAppComponent
import androidx.work.Configuration
import com.google.accompanist.insets.ExperimentalAnimatedInsets

@ExperimentalAnimationApi
@ExperimentalAnimatedInsets
@ExperimentalMaterialApi
class MainApplication : DaggerApplication(), Configuration.Provider {
    override fun onCreate() {

        // ThreeTenBP for times and dates, called before super to be available for objects
        AndroidThreeTen.init(this)

        // Enable strict mode before Dagger creates graph
        enableStrictMode()

        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out MainApplication> {
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

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    setMinimumLoggingLevel(android.util.Log.DEBUG)
                }
            }
            .build()
}
