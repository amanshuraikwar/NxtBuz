package io.github.amanshuraikwar.nxtbuz.data.busapi.model

import com.google.gson.annotations.SerializedName

data class BusArrivalsResponse(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("BusStopCode") val busStopCode: Int,
    @SerializedName("Services") val busArrivals: List<BusArrivalItem>
)