package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.medium
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.BusType

/**
 * Custom tailored view to display arriving bus data
 */
@Composable
internal fun ArrivingBusView(
    modifier: Modifier = Modifier,
    data: BusStopArrivalListItemData.BusStopArrival.Arriving,
    busServiceView: @Composable (busServiceNumber: String, isStarred: Boolean) -> Unit,
    busDestinationView: @Composable (busDestination: String) -> Unit,
    busArrivalView: @Composable (arrival: Int, busLoad: BusLoad?, busType: BusType?) -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            busServiceView(
                busServiceNumber = data.busServiceNumber,
                isStarred = data.starred
            )

            busDestinationView(busDestination = data.destinationBusStopDescription)

            busArrivalView(
                arrival = data.arrivingBusList.getOrNull(0)?.arrival ?: -1,
                busLoad = data.arrivingBusList.getOrNull(0)?.load,
                busType =
                data.arrivingBusList.getOrNull(0)?.type
            )
            Icon(
                modifier = Modifier.padding(top = 8.dp),
                imageVector = Icons.Rounded.ArrowRight,
                contentDescription = "Next Bus Arrival",
                tint = MaterialTheme.colors.onSurface.medium
            )
            busArrivalView(
                arrival = data.arrivingBusList.getOrNull(1)?.arrival ?: -1,
                busLoad = data.arrivingBusList.getOrNull(1)?.load,
                busType =
                data.arrivingBusList.getOrNull(1)?.type
            )
            Icon(
                modifier = Modifier.padding(top = 8.dp),
                imageVector = Icons.Rounded.ArrowRight,
                contentDescription = "Next Bus Arrival",
                tint = MaterialTheme.colors.onSurface.medium
            )
            busArrivalView(
                arrival = data.arrivingBusList.getOrNull(2)?.arrival ?: -1,
                busLoad = data.arrivingBusList.getOrNull(2)?.load,
                busType =
                data.arrivingBusList.getOrNull(2)?.type
            )
        }
    ) { measurables, constraints ->
        var busTimingsWidth = 0
        var busTimingsHeight = 0
        val busTimingsPlaceables = mutableListOf<Placeable>()
        for (i in 2..6) {
            val placeable = measurables[i].measure(constraints)
            busTimingsWidth += placeable.width
            busTimingsHeight = busTimingsHeight.coerceAtLeast(placeable.height)
            busTimingsPlaceables.add(placeable)
        }

        var height = busTimingsHeight

        val busServicePlaceable = measurables[0].measure(constraints)
        val busDestinationPlaceable = measurables[1].measure(
            constraints.copy(
                maxWidth = constraints.maxWidth
                        - busTimingsWidth
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
            var dx = 0
            for (i in 4 downTo 0) {
                val placeable = busTimingsPlaceables[i]
                placeable.place(
                    IntOffset(
                        constraints.maxWidth - dx - placeable.width,
                        (height - busTimingsHeight) / 2
                    )
                )
                dx += placeable.width
            }

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