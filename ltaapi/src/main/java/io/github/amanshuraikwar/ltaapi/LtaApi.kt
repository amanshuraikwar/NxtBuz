package io.github.amanshuraikwar.ltaapi

import io.github.amanshuraikwar.ltaapi.model.BusArrivalsResponseRetrofitDto
import io.github.amanshuraikwar.ltaapi.model.BusRoutesResponseRetrofitDto
import io.github.amanshuraikwar.ltaapi.model.BusStopsResponseRetrofitDto
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

internal interface LtaApi {
    @GET("BusStops")
    suspend fun getBusStops(@Query("`$`skip") skip: Int = 0): BusStopsResponseRetrofitDto

    @GET("BusArrivalv2")
    suspend fun getBusArrivals(
        @Query("BusStopCode") busStopCode: String,
        @Query("ServiceNo") busServiceNumber: String? = null
    ): BusArrivalsResponseRetrofitDto

    @GET("BusRoutes")
    suspend fun getBusRoutes(@Query("`$`skip") skip: Int = 0): BusRoutesResponseRetrofitDto

    companion object {
        private const val ENDPOINT = "http://datamall2.mytransport.sg/ltaodataservice/"

        internal fun createInstance(
            addLoggingInterceptors: Boolean,
            ltaAccountKey: String,
        ): LtaApi {
            return Retrofit
                .Builder()
                .baseUrl(ENDPOINT)
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
                                    .addHeader("AccountKey", ltaAccountKey)
                                    .url(newUrl)
                                    .build()

                            chain.proceed(newRequest)
                        }
                        .apply {
                            // log the api requests' body for debug and internal builds
                            @Suppress("ConstantConditionIf")
                            if (addLoggingInterceptors) {
                                addInterceptor(
                                    HttpLoggingInterceptor()
                                        .setLevel(HttpLoggingInterceptor.Level.BODY)
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
}