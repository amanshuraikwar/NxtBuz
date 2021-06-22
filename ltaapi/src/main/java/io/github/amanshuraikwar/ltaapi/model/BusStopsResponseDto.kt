package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

data class BusStopsResponseDto(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busStops: List<BusStopItemDto>
)