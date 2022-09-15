package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

@Serializable
internal data class TrainInfoDto(
    val bron: String,
    val ritnummer: Int,
    val station: String,
    val type: String,
    val vervoerder: String,
    val spoor: String,
    val materieeldelen: List<TrainInfoMaterialDto>,
    val ingekort: Boolean,
    val lengte: Int,
    val lengteInMeters: Int,
    val debug: List<String>,
)

@Serializable
internal data class TrainInfoMaterialDto(
    val materieelnummer: Int,
    val type: Int,
    val faciliteiten: List<String>,
    val afbeelding: String,
    val breedte: String,
    val hoogte: String,
    val bakken: List<TrainInfoMaterialBakingDto>
)

@Serializable
internal data class TrainInfoMaterialBakingDto(
    val afbeelding: TrainInfoMaterialBakingImageDto
)

@Serializable
internal data class TrainInfoMaterialBakingImageDto(
    val url: String,
    val breedte: Int,
    val hoogte: Int,
)