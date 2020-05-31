package io.github.amanshuraikwar.nxtbuz.data.busapi.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import io.github.amanshuraikwar.nxtbuz.data.busapi.SgBusApi
import io.github.amanshuraikwar.nxtbuz.util.flipper.FlipperHelper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class BusApiProvides {

    @Provides
    @Singleton
    fun a(): SgBusApi {
        return Retrofit
            .Builder()
            .baseUrl(SgBusApi.ENDPOINT)
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->
                        val request = chain.request()
                        var string: String = request.url.toString()
                        string = string.replace("%60%24%60", "$")
                        val newRequest = Request
                            .Builder()
                            .addHeader("AccountKey", BuildConfig.ltaAccountkey)
                            .url(string)
                            .build()
                        chain.proceed(newRequest)
                    }
                    .apply {
                        @Suppress("ConstantConditionIf")
                        if (BuildConfig.BUILD_TYPE == "debug"
                            || BuildConfig.BUILD_TYPE == "internal") {
                            addInterceptor(
                                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                            )
                        }
                    }
                    .apply {
                        FlipperHelper.addInterceptor(this)
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SgBusApi::class.java)
    }
}