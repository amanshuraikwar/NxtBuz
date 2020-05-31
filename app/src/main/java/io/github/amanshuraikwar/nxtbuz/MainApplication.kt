package io.github.amanshuraikwar.nxtbuz

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.amanshuraikwar.nxtbuz.di.DaggerAppComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


/**
 * Initialization of libraries.
 */
class MainApplication : DaggerApplication() {

    @Inject
    lateinit var networkFlipperPlugin: NetworkFlipperPlugin

    override fun onCreate() {

        // ThreeTenBP for times and dates, called before super to be available for objects
        AndroidThreeTen.init(this)

        // Enable strict mode before Dagger creates graph
        enableStrictMode()

        super.onCreate()

        // init flipper after Dagger creates graph
        initFlipper()
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    override fun applicationInjector(): AndroidInjector<out MainApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    private fun initFlipper() {
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(networkFlipperPlugin)
            client.addPlugin(DatabasesFlipperPlugin(this))
            client.start()
        }
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
