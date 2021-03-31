package io.github.amanshuraikwar.nxtbuz.starred

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals

@Composable
fun StarredBusArrivals(
    modifier: Modifier = Modifier,
) {
//    val listItems = listOf(
//
//    )

    LazyRow(
        modifier,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        item {
            BusArrivalItem(
                "Opp Blk 19",
                "961M",
                Arrivals.NotOperating,
            )

            Spacer(Modifier.size(16.dp))
        }

        item {
            BusArrivalItem(
                "Opp Blk 19",
                "961M",
                Arrivals.DataNotAvailable,
            )

            Spacer(Modifier.size(16.dp))
        }

        item {
            BusArrivalItem(
                "Opp Blk 19",
                "961M",
                Arrivals.NotOperating,
            )

            Spacer(Modifier.size(16.dp))
        }
    }
}