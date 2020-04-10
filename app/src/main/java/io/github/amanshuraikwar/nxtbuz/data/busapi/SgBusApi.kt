package io.github.amanshuraikwar.nxtbuz.data.busapi

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface SgBusApi {

    @GET("BusStops")
    suspend fun getBusStops(@Query("`$`skip") skip: Int = 0): BusStopsResponse

    @GET("BusArrivalv2")
    suspend fun getBusArrivals(@Query("BusStopCode") busStopCode: String): BusArrivalsResponse

    @GET("BusRoutes")
    suspend fun getBusRoutes(@Query("`$`skip") skip: Int = 0): BusRoutesResponse

}

data class BusStopsResponse(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busStops: List<BusStopItem>
)

data class BusStopItem(
    @SerializedName("BusStopCode") val code: String,
    @SerializedName("RoadName") val roadName: String,
    @SerializedName("Description") val description: String,
    @SerializedName("Latitude") val latitude: Double,
    @SerializedName("Longitude") val longitude: Double
)

data class BusArrivalsResponse(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("BusStopCode") val busStopCode: Int,
    @SerializedName("Services") val busArrivals: List<BusArrivalItem>
)

data class BusArrivalItem(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("NextBus") val arrivingBus: ArrivingBusItem?,
    @SerializedName("NextBus2") val arrivingBus1: ArrivingBusItem?,
    @SerializedName("NextBus3") val arrivingBus2: ArrivingBusItem?
)

data class ArrivingBusItem(
    @SerializedName("OriginCode") val originCode: String,
    @SerializedName("DestinationCode") val destinationCode: String,
    @SerializedName("EstimatedArrival") val estimatedArrival: String,
    @SerializedName("Latitude") val latitude: String,
    @SerializedName("Longitude") val longitude: String,
    @SerializedName("VisitNumber") val visitNumber: String,
    @SerializedName("Load") val load: String,
    @SerializedName("Feature") val feature: String,
    @SerializedName("Type") val type: String
)

data class BusRoutesResponse(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busRouteList: List<BusRouteItem>
)

data class BusRouteItem(
    @SerializedName("ServiceNo") val serviceNumber: String,
    @SerializedName("Operator") val operator: String,
    @SerializedName("Direction") val direction: Int,
    @SerializedName("StopSequence") val stopSequence: Int,
    @SerializedName("BusStopCode") val busStopCode: String,
    @SerializedName("Distance") val distance: Double,
    @SerializedName("WD_FirstBus") val wdFirstBus: String,
    @SerializedName("WD_LastBus") val wdLastBus: String,
    @SerializedName("SAT_FirstBus") val satFirstBus: String,
    @SerializedName("SAT_LastBus") val satLastBus: String,
    @SerializedName("SUN_FirstBus") val sunFirstBus: String,
    @SerializedName("SUN_LastBus") val sunLastBus: String
)