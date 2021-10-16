package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BusArrivalsResponseDto(
    @SerialName("odata.metadata") val metadata: String,
    @SerialName("BusStopCode") val busStopCode: Int,
    @SerialName("Services") val busArrivals: List<BusArrivalItemDto>
)