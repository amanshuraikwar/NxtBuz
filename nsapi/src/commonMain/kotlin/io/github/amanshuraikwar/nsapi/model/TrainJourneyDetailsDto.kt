package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
internal data class TrainJourneyDetailsResponseDto(
    val payload: TrainJourneyDetailsDto
)

@Serializable
internal data class TrainJourneyDetailsDto(
    val notes: List<TrainJourneyDetailsNoteDto>,
    val productNumbers: List<String>,
    val stops: List<TrainJourneyDetailsStopDto>,
    val allowCrowdReporting: Boolean,
    val source: String,
)

@Serializable
internal data class TrainJourneyDetailsNoteDto(
    val text: String,
    val noteType: String,
    val type: String,
)

@Serializable
internal data class TrainJourneyDetailsStopDto(
    // seems like this is suffixed by "_<digit>" ??
    val id: String,
    val stop: TrainJourneyDetailsStopDetailsDto,
    val previousStopId: List<String>,
    val nextStopId: List<String>,
    val destination: String? = null,
    // ORIGIN, PASSING, STOP, DESTINATION
    val status: String,
    // seems contains 0 or 1 item only
    val arrivals: List<TrainJourneyDetailsStopArrivalDto>,
    // seems contains 0 or 1 item only
    val departures: List<TrainJourneyDetailsStopDepartureDto>,
    val actualStock: TrainJourneyDetailsStopStockDto? = null,
    val plannedStock: TrainJourneyDetailsStopStockDto? = null,
    // seems always empty?
    val platformFeatures: List<String> = emptyList(),
    // seems always empty?
    val coachCrowdForecast: List<String> = emptyList(),
)

@Serializable
internal data class TrainJourneyDetailsStopStockDto(
    val trainType: String? = null,
    val numberOfSeats: Int,
    val numberOfParts: Int,
    val trainParts: List<TrainJourneyDetailsStopStockTrainPartDto>,
    val hasSignificantChange: Boolean,
)

@Serializable
internal data class TrainJourneyDetailsStopStockTrainPartDto(
    val stockIdentifier: String,
    // WIFI, TOILET, STILTE, FIETS
    val facilities: List<String>,
    val image: TrainJourneyDetailsStopStockTrainPartImageDto? = null
)

@Serializable
internal data class TrainJourneyDetailsStopStockTrainPartImageDto(
    val uri: String
)

@Serializable
internal data class TrainJourneyDetailsStopArrivalDto(
    val product: TrainJourneyDetailsStopProductDto,
    val origin: TrainJourneyDetailsStopDetailsDto,
    val destination: TrainJourneyDetailsStopDetailsDto? = null,
    val plannedTime: String,
    val actualTime: String? = null,
    val delayInSeconds: Int? = null,
    val plannedTrack: String,
    val actualTrack: String? = null,
    val cancelled: Boolean,
    val punctuality: Double? = null,
    // MEDIUM, LOW, UNKNOWN
    val crowdForecast: String,
    val stockIdentifiers: List<String> = emptyList()
)

@Serializable
internal data class TrainJourneyDetailsStopDepartureDto(
    val product: TrainJourneyDetailsStopProductDto,
    val origin: TrainJourneyDetailsStopDetailsDto,
    val destination: TrainJourneyDetailsStopDetailsDto? = null,
    val plannedTime: String,
    val actualTime: String? = null,
    val delayInSeconds: Int? = null,
    val plannedTrack: String,
    val actualTrack: String? = null,
    val cancelled: Boolean,
    // MEDIUM, UNKNOWN
    val crowdForecast: String,
    val stockIdentifiers: List<String> = emptyList()
)

@Serializable
internal data class TrainJourneyDetailsStopProductDto(
    val number: String,
    val categoryCode: String,
    val shortCategoryName: String,
    val longCategoryName: String,
    val operatorCode: String,
    val operatorName: String,
    // TRAIN
    val type: String,
)

@Serializable
internal data class TrainJourneyDetailsStopDetailsDto(
    val name: String,
    val lng: Double,
    val lat: Double,
    // NL -> The Netherlands
    // D -> Deutschland / Germany
    val countryCode: String,
    val uicCode: String,
)