package io.github.amanshuraikwar.nxtbuz.data.busapi

import io.github.amanshuraikwar.nxtbuz.data.busapi.model.BusArrivalsResponse
import io.github.amanshuraikwar.nxtbuz.data.busapi.model.BusRoutesResponse
import io.github.amanshuraikwar.nxtbuz.data.busapi.model.BusStopsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SgBusApi {

    @GET("BusStops")
    suspend fun getBusStops(@Query("`$`skip") skip: Int = 0): BusStopsResponse

    @GET("BusArrivalv2")
    suspend fun getBusArrivals(
        @Query("BusStopCode") busStopCode: String,
        @Query("ServiceNo") busServiceNumber: String? = null
    ): BusArrivalsResponse

    @GET("BusRoutes")
    suspend fun getBusRoutes(@Query("`$`skip") skip: Int = 0): BusRoutesResponse

    companion object {
        const val ENDPOINT = "http://datamall2.mytransport.sg/ltaodataservice/"
    }
}













