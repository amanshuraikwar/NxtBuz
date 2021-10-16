package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

internal data class BusArrivalsResponseRetrofitDto(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("BusStopCode") val busStopCode: Int,
    @SerializedName("Services") val busArrivals: List<BusArrivalItemRetrofitDto>
)