package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

internal data class BusStopsResponseRetrofitDto(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busStops: List<BusStopItemRetrofitDto>
)