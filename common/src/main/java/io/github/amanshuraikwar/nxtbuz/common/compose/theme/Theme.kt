/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.amanshuraikwar.nxtbuz.common.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = blueLighter,
    primaryVariant = blue,
    secondary = blueLighter,
    background = black,
    surface = black,
    error = redLighter,
    onPrimary = gray900,
    onSecondary = gray900,
    onBackground = blueGrey50,
    onSurface = blueGrey50,
    onError = gray900
)

private val LightColorPalette = lightColors(
    primary = blue,
    primaryVariant = blueDark,
    secondary = blue,
    secondaryVariant = blueDark,
    background = blueGrey50,
    surface = white,
    error = red,
    onPrimary = blueGrey50,
    onSecondary = blueGrey50,
    onBackground = blueGreyDark,
    onSurface = blueGreyDark,
    onError = blueGrey50
)

val Colors.star: Color
    get() = if (isLight) yellow else yellowLighter

val Colors.onStar: Color
    get() = if (isLight) this.onSurface else this.onPrimary

val Color.medium: Color
    get() = this.copy(alpha = 0.74f)

val Color.disabled: Color
    get() = this.copy(alpha = 0.38f)

val Colors.outline: Color
    get() = this.onSurface.copy(alpha = 0.12f)

val Colors.singapore: Color
    get() = if (isLight) red else redLighter

@Composable
fun NxtBuzTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    android.graphics.Color.parseColor("color").toULong() shl 32

    MaterialTheme(
        colors = colors,
        typography = NxtBuzTypography,
        shapes = NxtBuzThemeShapes,
        content = content
    )
}
