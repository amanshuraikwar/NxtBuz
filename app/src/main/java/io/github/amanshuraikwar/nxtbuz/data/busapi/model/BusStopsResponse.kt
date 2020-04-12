package io.github.amanshuraikwar.nxtbuz.data.busapi.model

import com.google.gson.annotations.SerializedName

data class BusStopsResponse(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busStops: List<BusStopItem>
)