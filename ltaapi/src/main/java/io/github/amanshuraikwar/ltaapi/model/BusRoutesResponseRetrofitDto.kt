package io.github.amanshuraikwar.ltaapi.model

import com.google.gson.annotations.SerializedName

internal data class BusRoutesResponseRetrofitDto(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busRouteList: List<BusRouteItemRetrofitDto>
)