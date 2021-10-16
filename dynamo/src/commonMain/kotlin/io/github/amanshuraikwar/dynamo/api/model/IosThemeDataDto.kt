package io.github.amanshuraikwar.dynamo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class IosThemeDataDto(
    val darkThemeColors: IosThemeColorsDto,
    val lightThemeColors: IosThemeColorsDto
)