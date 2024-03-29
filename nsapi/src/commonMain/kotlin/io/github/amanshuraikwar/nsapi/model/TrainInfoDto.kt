package io.github.amanshuraikwar.nsapi.model

import kotlinx.serialization.Serializable

internal sealed class TrainInfoResponseDto {
    @Serializable
    data class Error(val errors: List<TrainInfoErrorDto>) : TrainInfoResponseDto()

    @Serializable
    data class Success(val info: List<TrainInfoDto>) : TrainInfoResponseDto()
}

@Serializable
internal data class TrainInfoErrorResponseDto(
    val errors: List<TrainInfoErrorDto>
)

@Serializable
internal data class TrainInfoErrorDto(
    val message: String,
    val code: Int
)

@Serializable
internal data class TrainInfoDto(
    // source
    val bron: String,
    // ride number / train number
    val ritnummer: Int,
    // current station
    val station: String,
    // train type code
    // for eg: ICD (intercity direct), ICM (intercity), SNG (sprinter), SLT (sprinter),
    //         TGV (french bullet), ICE (german bullet)
    val type: String? = null,
    // operator
    // for eg: NS
    val vervoerder: String? = null,
    // track
    val spoor: String? = null,
    // material info / rolling stock info
    val materieeldelen: List<TrainInfoMaterialDto>,
    // shortened
    val ingekort: Boolean,
    // length
    val lengte: Int,
    val lengteInMeters: Int,
    val debug: List<String> = emptyList(),
)

@Serializable
internal data class TrainInfoMaterialDto(
    // this can be null in case of front and rear engines
    val materieelnummer: Int? = null,
    val type: String,
    // TOILET, STROOM (power sockets), WIFI, STILTE (silence), FIETS (bicycle),
    // TOEGANKELIJK (accessible)
    val faciliteiten: List<String>,
    // image url of complete rolling stock
    val afbeelding: String? = null,
    // width
    val breedte: Int? = null,
    // height
    val hoogte: Int? = null,
    // info of individual coaches of the rolling stock
    // can be empty in case of front and read engines
    val bakken: List<TrainInfoMaterialBakingDto>
)

@Serializable
internal data class TrainInfoMaterialBakingDto(
    val afbeelding: TrainInfoMaterialBakingImageDto? = null
)

@Serializable
internal data class TrainInfoMaterialBakingImageDto(
    val url: String,
    // width
    val breedte: Int,
    // height
    val hoogte: Int,
)