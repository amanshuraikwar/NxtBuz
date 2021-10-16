package io.github.amanshuraikwar.dynamo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class IosThemeColorsDto(
    val accent: String,
    val primary: String,
    val secondary: String
)