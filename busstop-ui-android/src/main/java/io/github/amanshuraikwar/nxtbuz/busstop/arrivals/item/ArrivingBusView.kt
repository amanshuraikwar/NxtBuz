package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
internal fun ArrivingBusView(
    modifier: Modifier = Modifier,
    busServiceView: @Composable () -> Unit,
    busDestinationView: @Composable () -> Unit,
    busTimingsView: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            busServiceView()
            busDestinationView()
            busTimingsView()
        }
    ) { measurables, constraints ->
        val busTimingsPlaceable = measurables.last().measure(constraints)
        var height = busTimingsPlaceable.height

        val busServicePlaceable = measurables[0].measure(constraints)
        val busDestinationPlaceable = measurables[1].measure(
            constraints.copy(
                maxWidth = constraints.maxWidth
                        - busTimingsPlaceable.width
                        - 4.dp.roundToPx()
            )
        )

        height = height.coerceAtLeast(
            busServicePlaceable.height
                    + busDestinationPlaceable.height
                    + 4.dp.roundToPx()
        )

        layout(
            width = constraints.maxWidth,
            height = height,
        ) {
            busTimingsPlaceable.place(
                IntOffset(
                    constraints.maxWidth - busTimingsPlaceable.width,
                    (height - busTimingsPlaceable.height) / 2
                )
            )

            busServicePlaceable.place(
                IntOffset(
                    0,
                    0
                )
            )

            busDestinationPlaceable.place(
                IntOffset(
                    0,
                    height - busDestinationPlaceable.height
                )
            )
        }
    }
}