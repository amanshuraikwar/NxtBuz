package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BusStopsResponseDto(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("value") val busStops: List<BusStopItemDto>
)