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
package io.github.amanshuraikwar.nxtbuz.common.compose.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme

@Composable
fun PreviewSurface(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    NxtBuzTheme(darkTheme) {
        Surface(
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}
