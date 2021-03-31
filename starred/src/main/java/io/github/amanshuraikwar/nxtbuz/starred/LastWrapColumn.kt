package io.github.amanshuraikwar.nxtbuz.starred

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset

@Composable
fun LastWrapColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val lastPlaceable = measurables.last().measure(constraints.copy())

        var height = lastPlaceable.height

        val placeables = measurables.dropLast(1).map { measurable ->
            measurable.measure(
                constraints.copy(
                    minWidth = lastPlaceable.width,
                    maxWidth = lastPlaceable.width,
                )
            ).also { placeable ->
                height += placeable.height
            }
        }

        layout(
            width = lastPlaceable.width,
            height = height,
        ) {
            var offsetY = 0

            placeables.forEach { placeable ->
                placeable.place(
                    IntOffset(0, offsetY)
                )
                offsetY += placeable.height
            }

            lastPlaceable.place(
                IntOffset(0, offsetY)
            )
        }
    }
}