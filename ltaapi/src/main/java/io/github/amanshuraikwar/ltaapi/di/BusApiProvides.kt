package io.github.amanshuraikwar.ltaapi.di

import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.ltaapi.BuildConfig
import io.github.amanshuraikwar.ltaapi.LtaApi
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
    fun a(): LtaApi {
        return Retrofit
            .Builder()
            .baseUrl(LtaApi.ENDPOINT)
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor { chain ->

                        // replace the encoded '$' back to normal
                        // weird query param names required by the api -_-
                        val newUrl =
                            chain.request()
                                .url.toString()
                                .replace("%60%24%60", "$")

                        // add account key aka the api key
                        val newRequest =
                            Request.Builder()
                                .addHeader("AccountKey", BuildConfig.ltaAccountkey)
                                .url(newUrl)
                                .build()

                        chain.proceed(newRequest)
                    }
                    .apply {

                        // log the api requests' body for debug and internal builds
                        @Suppress("ConstantConditionIf")
                        if (BuildConfig.BUILD_TYPE == "debug"
                            || BuildConfig.BUILD_TYPE == "internal"
                        ) {
                            addInterceptor(
                                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                            )
                        }
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LtaApi::class.java)
    }
}