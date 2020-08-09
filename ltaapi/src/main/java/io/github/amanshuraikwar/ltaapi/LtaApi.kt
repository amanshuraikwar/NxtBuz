package io.github.amanshuraikwar.ltaapi

import io.github.amanshuraikwar.ltaapi.model.BusArrivalsResponseDto
import io.github.amanshuraikwar.ltaapi.model.BusRoutesResponseDto
import io.github.amanshuraikwar.ltaapi.model.BusStopsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface LtaApi {

    @GET("BusStops")
    suspend fun getBusStops(@Query("`$`skip") skip: Int = 0): BusStopsResponseDto

    @GET("BusArrivalv2")
    suspend fun getBusArrivals(
        @Query("BusStopCode") busStopCode: String,
        @Query("ServiceNo") busServiceNumber: String? = null
    ): BusArrivalsResponseDto

    @GET("BusRoutes")
    suspend fun getBusRoutes(@Query("`$`skip") skip: Int = 0): BusRoutesResponseDto

    companion object {
        const val ENDPOINT = "http://datamall2.mytransport.sg/ltaodataservice/"


    }
}