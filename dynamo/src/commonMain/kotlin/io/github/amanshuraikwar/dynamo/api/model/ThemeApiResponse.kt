package io.github.amanshuraikwar.dynamo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ThemeApiResponse(
    val ios: IosThemeDataDto
)