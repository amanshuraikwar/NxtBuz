package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
internal data class StationsResponseDto(
    val payload: List<StationDto>
)

@Serializable
internal data class StationDto(
    val UICCode: String,
    val stationType: String,
    val EVACode: String,
    val code: String,
    val sporen: List<SporenDto>,
    val synoniemen: List<String>,
    val heeftFaciliteiten: Boolean,
    val heeftVertrektijden: Boolean,
    val heeftReisassistentie: Boolean,
    val namen: StationNameDto,
    val land: String,
    val lat: Double,
    val lng: Double,
    val radius: Double,
    val naderenRadius: Double,
    val ingangsDatum: String,
    val nearbyMeLocationId: NearbyMeLocationIdDto
)

@Serializable
internal data class StationNameDto(
    val lang: String,
    val middel: String,
    val kort: String
)

@Serializable
internal data class NearbyMeLocationIdDto(
    val value: String,
    val type: String
)

@Serializable
internal data class SporenDto(
    val spoorNummer: String
)