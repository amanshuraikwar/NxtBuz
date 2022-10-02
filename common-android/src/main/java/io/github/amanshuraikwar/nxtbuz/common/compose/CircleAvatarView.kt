package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

enum class CircleAvatarPosition { ORIGIN, DESTINATION, MIDDLE }

@Composable
fun CircleAvatarItem(
    modifier: Modifier = Modifier,
    circleColor: Color = MaterialTheme.colors.primary,
    topBarColor: Color = MaterialTheme.colors.onSurface,
    bottomBarColor: Color = circleColor,
    position: CircleAvatarPosition = CircleAvatarPosition.MIDDLE,
    onAvatarClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier
            .fillMaxWidth(),
        content = {
            Canvas(
                modifier = Modifier.clickable(onClick = onAvatarClick),
            ) {
                if (position != CircleAvatarPosition.ORIGIN) {
                    drawRect(
                        color = topBarColor,
                        topLeft = Offset(center.x - 2.dp.toPx(), 0f),
                        size = Size(4.dp.toPx(), size.height / 2)
                    )
                }

                if (position != CircleAvatarPosition.DESTINATION) {
                    drawRect(
                        color = bottomBarColor,
                        topLeft = Offset(center.x - 2.dp.toPx(), size.height / 2),
                        size = Size(4.dp.toPx(), size.height / 2)
                    )
                }

                drawCircle(
                    color = circleColor,
                    center = Offset(center.x, center.y),
                    radius = 8.dp.toPx()
                )
            }

            content()
        }
    ) { measurables, constraints ->
        val textPlaceable = measurables[1].measure(
            constraints.copy(
                minWidth = constraints.maxWidth - 72.dp.toPx().toInt() - 16.dp.toPx().toInt(),
                maxWidth = constraints.maxWidth - 72.dp.toPx().toInt() - 16.dp.toPx().toInt(),
            )
        )

        val canvasPlaceable = measurables[0].measure(
            constraints.copy(
                minWidth = 72.dp.toPx().toInt(),
                maxWidth = 72.dp.toPx().toInt(),
                minHeight = textPlaceable.height + 16.dp.toPx().toInt() + 16.dp.toPx().toInt(),
                maxHeight = textPlaceable.height + 16.dp.toPx().toInt() + 16.dp.toPx().toInt(),
            )
        )

        layout(
            width = constraints.maxWidth,
            height = textPlaceable.height + 16.dp.toPx().toInt() + 16.dp.toPx().toInt()
        ) {
            textPlaceable.place(72.dp.toPx().toInt(), 16.dp.toPx().toInt())
            canvasPlaceable.place(x = 0, y = 0)
        }
    }
}