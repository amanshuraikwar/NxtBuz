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
                        val request = chain.request()
                        var string: String = request.url.toString()
                        string = string.replace("%60%24%60", "$")
                        val newRequest = Request
                            .Builder()
                                //todo
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
//                    .apply {
//                        FlipperHelper.addInterceptor(this)
//                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LtaApi::class.java)
    }
}