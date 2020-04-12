package io.github.amanshuraikwar.nxtbuz.data.busapi.model

import com.google.gson.annotations.SerializedName

data class BusRoutesResponse(
    @SerializedName("odata.metadata") val metadata: String,
    @SerializedName("value") val busRouteList: List<BusRouteItem>
)