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

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.amanshuraikwar.nxtbuz.common.R

private val RubikFontFamily = FontFamily(
    Font(R.font.rubik),
    Font(R.font.rubik_bold, FontWeight.Bold),
    Font(R.font.rubik_medium, FontWeight.Medium)
)

private val CabinFontFamily = FontFamily(
    Font(R.font.cabin),
)

val NxtBuzTypography = Typography(
    defaultFontFamily = RubikFontFamily,
    h3 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 48.sp,
        letterSpacing = 0.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp,
        fontFamily = CabinFontFamily
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        fontFamily = CabinFontFamily
    ),
    overline = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp,
        fontFamily = CabinFontFamily
    )
)

val Typography.body1Bold: TextStyle
    get() = body1.copy(fontWeight = FontWeight.Bold)

val Typography.h6Bold: TextStyle
    get() = h6.copy(fontWeight = FontWeight.Bold)
