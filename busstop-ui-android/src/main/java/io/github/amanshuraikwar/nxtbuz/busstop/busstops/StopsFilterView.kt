package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderButton
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.StopsFilter

@Composable
fun StopsFilterView(
    modifier: Modifier = Modifier,
    filter: StopsFilter,
    onStopsFilterClick: (StopsFilter) -> Unit
) {
    Row(
        modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        HeaderButton(
            Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            imageVector = Icons.Rounded.DirectionsBus,
            text = "Bus Stops",
            onClick = {
                onStopsFilterClick(StopsFilter.BUS_STOPS_ONLY)
            },
            selected = filter == StopsFilter.BUS_STOPS_ONLY
        )

        HeaderButton(
            Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            imageVector = Icons.Rounded.Train,
            text = "Train Stops",
            onClick = {
                onStopsFilterClick(StopsFilter.TRAIN_STOPS_ONLY)
            },
            selected = filter == StopsFilter.TRAIN_STOPS_ONLY
        )
    }
}