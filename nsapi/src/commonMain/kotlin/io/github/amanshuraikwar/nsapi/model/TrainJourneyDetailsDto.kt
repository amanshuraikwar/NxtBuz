package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
internal data class TrainJourneyDetailsResponseDto(
    val payload: TrainJourneyDetailsDto
)

@Serializable
internal data class TrainJourneyDetailsDto(
    val notes: List<String>,
    val productNumbers: List<String>,
    val stops: List<TrainJourneyDetailsStopDto>,
    val allowCrowdReporting: Boolean,
    val source: String,
)

@Serializable
internal data class TrainJourneyDetailsStopDto(
    val id: String,
    val stop: TrainJourneyDetailsStopDetailsDto,
    val previousStopId: List<String>,
    val nextStopId: List<String>,
    val destination: String,
    // ORIGIN, PASSING, STOP
    val status: String,
    val arrivals: List<TrainJourneyDetailsStopArrivalDto>,
    val departures: List<TrainJourneyDetailsStopDepartureDto>,
    val actualStock: TrainJourneyDetailsStopStockDto,
    val plannedStock: TrainJourneyDetailsStopStockDto,
    val platformFeatures: List<String>,
    val coachCrowdForecast: List<String>,
)

@Serializable
internal data class TrainJourneyDetailsStopStockDto(
    val trainType: String,
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
    val image: TrainJourneyDetailsStopStockTrainPartImageDto
)

@Serializable
internal data class TrainJourneyDetailsStopStockTrainPartImageDto(
    val url: String
)

@Serializable
internal data class TrainJourneyDetailsStopArrivalDto(
    val product: TrainJourneyDetailsStopProductDto,
    val origin: TrainJourneyDetailsStopDetailsDto,
    val destination: TrainJourneyDetailsStopDetailsDto,
    val plannedTime: String,
    val actualTime: String,
    val delayInSeconds: Int,
    val plannedTrack: String,
    val actualTrack: String,
    val cancelled: Boolean,
    val punctuality: Double,
    // MEDIUM
    val crowdForecast: String,
    val stockIdentifiers: List<String>
)

@Serializable
internal data class TrainJourneyDetailsStopDepartureDto(
    val product: TrainJourneyDetailsStopProductDto,
    val origin: TrainJourneyDetailsStopDetailsDto,
    val destination: TrainJourneyDetailsStopDetailsDto,
    val plannedTime: String,
    val actualTime: String,
    val delayInSeconds: Int,
    val plannedTrack: String,
    val actualTrack: String,
    val cancelled: Boolean,
    // MEDIUM
    val crowdForecast: String,
    val stockIdentifiers: List<String>
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
    val countryCode: String,
    val uicCode: String,
)