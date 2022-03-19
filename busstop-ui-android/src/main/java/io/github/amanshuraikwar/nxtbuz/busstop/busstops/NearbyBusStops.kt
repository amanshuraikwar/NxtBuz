package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.items.BusStopItem
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun NearbyBusStops(
    listItems: List<BusStopsItemData>,
    padding: PaddingValues,
    onBusStopClick: (busStopCode: String) -> Unit,
    onBusStopStarToggle: (busStopCode: String, newStarState: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(null) {
        lazyListState.scrollToItem(0)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            bottom = 128.dp,
            top = padding.calculateTopPadding()
        ),
        state = lazyListState,
    ) {
        itemsIndexedSafe(
            items = listItems,
            key = { _, item ->
                when (item) {
                    is BusStopsItemData.BusStop -> item.id
                    is BusStopsItemData.Header -> item.id
                }
            },
            errorKey = "bus-route-arrivals-error-key",
        ) { _, item ->
            when (item) {
                is BusStopsItemData.BusStop -> {
                    BusStopItem(
                        data = item,
                        onClick = {
                            coroutineScope.launch {
                                onBusStopClick(item.busStopCode)
                            }
                        },
                        onStarToggle = { newStarState ->
                            coroutineScope.launch {
                                onBusStopStarToggle(item.busStopCode, newStarState)
                            }
                        }
                    )
                }
                is BusStopsItemData.Header -> {
                    Header(title = item.title)
                }
            }
        }
    }
}