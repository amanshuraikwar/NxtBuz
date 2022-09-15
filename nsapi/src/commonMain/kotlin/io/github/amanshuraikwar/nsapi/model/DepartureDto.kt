package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
internal data class DeparturesResponseDto(
    val payload: DeparturesDto
)

@Serializable
internal data class DeparturesDto(
    val source: String,
    val departures: List<DepartureDto>
)

@Serializable
internal data class DepartureDto(
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
internal data class DepartureProductDto(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    val type: String
)

@Serializable
internal data class DepartureRouteStationDto(
    val uicCode: String,
    val mediumName: String
)

@Serializable
internal data class DepartureMessageDto(
    val message: String,
    val style: String
)
