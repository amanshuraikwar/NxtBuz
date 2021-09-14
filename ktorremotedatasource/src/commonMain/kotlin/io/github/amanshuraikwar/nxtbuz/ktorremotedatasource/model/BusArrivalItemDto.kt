package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BusArrivalItemDto(
    @SerialName("ServiceNo") val serviceNumber: String,
    @SerialName("Operator") val operator: String,
    @SerialName("NextBus") val arrivingBus: ArrivingBusItemDto?,
    @SerialName("NextBus2") val arrivingBus1: ArrivingBusItemDto?,
    @SerialName("NextBus3") val arrivingBus2: ArrivingBusItemDto?
)