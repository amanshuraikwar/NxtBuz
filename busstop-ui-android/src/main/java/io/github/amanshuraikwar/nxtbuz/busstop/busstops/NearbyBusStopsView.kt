package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.items.BusStopItemView
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.items.TrainStopItemView
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderView
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun NearbyBusStopsView(
    listItems: List<BusStopsItemData>,
    onBusStopClick: (busStopCode: String) -> Unit,
    onBusStopStarToggle: (busStopCode: String, newStarState: Boolean) -> Unit,
    onTrainStopClick: (trainsStopCode: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(null) {
        lazyListState.scrollToItem(0)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            bottom = 128.dp,
        ),
        state = lazyListState,
    ) {
        itemsIndexedSafe(
            items = listItems,
            key = { _, item ->
                when (item) {
                    is BusStopsItemData.BusStop -> item.id
                    is BusStopsItemData.Header -> item.id
                    is BusStopsItemData.TrainStop -> item.id
                }
            },
            errorKey = "bus-route-arrivals-error-key",
        ) { _, item ->
            when (item) {
                is BusStopsItemData.BusStop -> {
                    BusStopItemView(
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
                    HeaderView(title = item.title)
                }
                is BusStopsItemData.TrainStop -> {
                    TrainStopItemView(
                        data = item,
                        onClick = {
                            onTrainStopClick(item.code)
                        },
                        onStarToggle = {}
                    )
                }
            }
        }
    }
}
