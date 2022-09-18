package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp

@Composable
fun BarAvatarView(
    drawBar: Boolean = true,
    barColor: Color = MaterialTheme.colors.onSurface,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = Modifier.fillMaxWidth(),
        content = {
            Canvas(
                modifier = Modifier,
            ) {
                if (drawBar) {
                    drawRect(
                        color = barColor,
                        topLeft = Offset(center.x - 2.dp.toPx(), 0f),
                        size = Size(4.dp.toPx(), size.height)
                    )
                }
            }

            content()
        }
    ) { measurables, constraints ->
        val contentPlaceable = measurables[1].measure(
            constraints.copy(
                minWidth = constraints.maxWidth - 72.dp.toPx().toInt() - 16.dp.toPx().toInt(),
                maxWidth = constraints.maxWidth - 72.dp.toPx().toInt() - 16.dp.toPx().toInt(),
            )
        )

        val canvasPlaceable = measurables[0].measure(
            constraints.copy(
                minWidth = 72.dp.toPx().toInt(),
                maxWidth = 72.dp.toPx().toInt(),
                minHeight = contentPlaceable.height + 16.dp.toPx().toInt(),
                maxHeight = contentPlaceable.height + 16.dp.toPx().toInt(),
            )
        )

        layout(
            width = constraints.maxWidth,
            height = contentPlaceable.height + 16.dp.toPx().toInt(),
        ) {
            contentPlaceable.place(72.dp.toPx().toInt(), 0)
            canvasPlaceable.place(x = 0, y = 0)
        }
    }
}