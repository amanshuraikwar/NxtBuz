package io.github.amanshuraikwar.nxtbuz.util.flipper

import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.soloader.SoLoader
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import okhttp3.OkHttpClient

object FlipperHelper {

    private val networkFlipperPlugin: NetworkFlipperPlugin = NetworkFlipperPlugin()

    fun init(context: Context) {
        SoLoader.init(context, false)
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(context)) {
            val client = AndroidFlipperClient.getInstance(context)
            client.addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))
            client.addPlugin(networkFlipperPlugin)
            client.addPlugin(DatabasesFlipperPlugin(context))
            client.start()
        }
    }

    fun addInterceptor(builder: OkHttpClient.Builder) {
        builder.addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
    }
}