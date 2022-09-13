package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
data class ArrivalsResponseDto(
    val payload: ArrivalsDto
)

@Serializable
data class ArrivalsDto(
    val source: String,
    val arrivals: List<ArrivalDto>
)

@Serializable
data class ArrivalDto(
    val origin: String,
    val name: String,
    val plannedDateTime: String,
    val plannedTimeZoneOffset: Int,
    val actualDateTime: String,
    val actualTimeZoneOffset: Int,
    val plannedTrack: String,
    val product: ArrivalProductDto,
    val trainCategory: String,
    val cancelled: Boolean,
    val messages: List<ArrivalMessageDto>,
    val arrivalStatus: String
)

@Serializable
data class ArrivalProductDto(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    val type: String
)

@Serializable
data class ArrivalMessageDto(
    val message: String,
    val style: String
)