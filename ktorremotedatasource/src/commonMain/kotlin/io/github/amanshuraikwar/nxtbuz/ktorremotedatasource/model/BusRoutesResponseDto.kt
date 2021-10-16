package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BusRoutesResponseDto(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val busRouteList: List<BusRouteItemDto>
)