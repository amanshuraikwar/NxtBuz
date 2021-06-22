package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

@Composable
fun BusRoutePreviousAllItem(
    modifier: Modifier = Modifier,
    title: String
) {
    Box(
        modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        val color = MaterialTheme.colors.onSurface

        Canvas(
            modifier = Modifier
                .size(72.dp, 25.dp),
        ) {
            drawCircle(
                color = color,
                center = Offset(center.x, 2.dp.toPx()),
                radius = 2.dp.toPx()
            )

            drawCircle(
                color = color,
                center = Offset(center.x, 13.dp.toPx()),
                radius = 2.dp.toPx()
            )

            drawArc(
                color = color,
                startAngle = -180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(center.x - 2.dp.toPx(), 23.dp.toPx()),
                size = Size(4.dp.toPx(), 4.dp.toPx())
            )
        }

        Text(
            text = title,
            Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.body2,
        )
    }
}