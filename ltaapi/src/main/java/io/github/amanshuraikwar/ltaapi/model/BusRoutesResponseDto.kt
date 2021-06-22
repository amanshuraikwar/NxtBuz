package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

data class BusRoutesResponseDto(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busRouteList: List<BusRouteItemDto>
)