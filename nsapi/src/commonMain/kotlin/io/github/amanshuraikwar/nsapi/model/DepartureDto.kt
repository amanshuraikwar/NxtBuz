package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
data class DeparturesResponseDto(
    val payload: DeparturesDto
)

@Serializable
data class DeparturesDto(
    val source: String,
    val departures: List<DepartureDto>
)

@Serializable
data class DepartureDto(
    val direction: String,
    val name: String,
    val plannedDateTime: String,
    val plannedTimeZoneOffset: Int,
    val actualDateTime: String,
    val actualTimeZoneOffset: Int,
    val plannedTrack: String,
    val product: DepartureProductDto,
    val trainCategory: String,
    val cancelled: Boolean,
    val routeStations: List<DepartureRouteStationDto>,
    val messages: List<DepartureMessageDto>,
    val departureStatus: String
)

@Serializable
data class DepartureProductDto(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    val type: String
)

@Serializable
data class DepartureRouteStationDto(
    val uicCode: String,
    val mediumName: String
)

@Serializable
data class DepartureMessageDto(
    val message: String,
    val style: String
)
