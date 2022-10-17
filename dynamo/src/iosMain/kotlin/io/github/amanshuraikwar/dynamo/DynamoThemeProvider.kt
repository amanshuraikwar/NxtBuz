package io.github.amanshuraikwar.dynamo

import io.github.amanshuraikwar.dynamo.api.ThemeApi
import platform.UIKit.UIColor

class DynamoThemeProvider {
    fun createDynamoThemeRepository(
        defaultTheme: DynamoTheme = DEFAULT_THEME,
        enableThemeApiLogging: Boolean,
        themeApiUrl: String,
    ): DynamoThemeRepository {
        return DynamoThemeRepository(
            defaultTheme = defaultTheme,
            apiDataConverter = { themeApiResponse ->
                DynamoTheme(
                    lightThemeColors = DynamoThemeColors(
                        primary = themeApiResponse.ios.lightThemeColors.primary.toUiColor(),
                        secondary = themeApiResponse.ios.lightThemeColors.secondary.toUiColor(),
                        accent = themeApiResponse.ios.lightThemeColors.accent.toUiColor(),
                    ),
                    darkThemeColors = DynamoThemeColors(
                        primary = themeApiResponse.ios.darkThemeColors.primary.toUiColor(),
                        secondary = themeApiResponse.ios.darkThemeColors.secondary.toUiColor(),
                        accent = themeApiResponse.ios.darkThemeColors.accent.toUiColor(),
                    )
                )
            },
            themeApi = ThemeApi.createInstance(
                addLoggingInterceptors = enableThemeApiLogging,
                themeApiUrl = themeApiUrl,
            )
        )
    }

    companion object {
        val DEFAULT_THEME = DynamoTheme(
            darkThemeColors = DynamoThemeColors(
                primary = "#FFeef0f2".toUiColor(),
                secondary = "#FFB0BEC5".toUiColor(),
                accent = "#Ff1BB3F3".toUiColor()
            ),
            lightThemeColors = DynamoThemeColors(
                primary = "#FF263238".toUiColor(),
                secondary = "#ff455A64".toUiColor(),
                accent = "#FF186CE6".toUiColor()
            )
        )

        fun String.toUiColor(): UIColor {
            return this
                .drop(1)
                .let {
                    val a = it.substring(0..1).toLong(radix = 16).toDouble()
                    val r = it.substring(2..3).toLong(radix = 16).toDouble()
                    val g = it.substring(4..5).toLong(radix = 16).toDouble()
                    val b = it.substring(6..7).toLong(radix = 16).toDouble()
                    UIColor(
                        red = r / 255.0,
                        green = g / 255.0,
                        blue = b / 255.0,
                        alpha = a / 255.0,
                    )
                }
        }
    }
}