package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
internal data class TrainCrowdForecastStationDto(
    val station: String,
    val capaciteit: Int,
    val classifiction: String
)