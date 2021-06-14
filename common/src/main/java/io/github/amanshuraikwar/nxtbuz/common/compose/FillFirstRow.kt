package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

@Composable
fun FillFirstRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeable2 = measurables[1].measure(constraints.copy(minWidth = 0))
        val placeable1 = measurables[0].measure(
            constraints.copy(
                minWidth = constraints.maxWidth - placeable2.width,
                maxWidth = constraints.maxWidth - placeable2.width
            )
        )

        val width = constraints.maxWidth
        val height = placeable1.height.coerceAtLeast(placeable2.height)

        layout(width = width, height = height) {
            placeable1.place(x = 0, y = (height - placeable1.height) / 2)
            placeable2.place(x = placeable1.width, y = (height - placeable2.height) / 2)
        }
    }
}