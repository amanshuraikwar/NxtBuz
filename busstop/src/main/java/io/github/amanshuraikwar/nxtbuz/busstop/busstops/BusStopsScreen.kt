package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.items.BusStopItem
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.rememberNxtBuzBottomSheetState
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopsScreen(
    modifier: Modifier = Modifier,
    vm: BusStopsViewModel,
    bottomSheetBgOffset: Dp,
    onBusStopClick: (busStop: BusStop) -> Unit = {},
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        BottomSheetValue.Collapsed
    )

    val screenState by vm.screenState.collectAsState()

    LaunchedEffect(key1 = bottomSheetState.isInitialised) {
        if (bottomSheetState.isInitialised) {
            vm.fetchBusStops()
        }
    }

    NxtBuzBottomSheet(
        modifier = modifier,
        state = bottomSheetState,
        bottomSheetBgOffset = bottomSheetBgOffset
    ) { padding ->
        Crossfade(targetState = screenState) { screenState ->
            when (screenState) {
                BusStopsScreenState.Failed -> TODO()
                BusStopsScreenState.Fetching -> {
                    FetchingView(
                        Modifier
                            .fillMaxWidth()
                            .padding(paddingValues = padding)
                    )
                }
                is BusStopsScreenState.Success -> {
                    BusStopsView(
                        screenState.listItems,
                        padding,
                        onBusStopClick
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BusStopsView(
    listItems: List<BusStopsItemData>,
    padding: PaddingValues,
    onBusStopClick: (busStop: BusStop) -> Unit = {},
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
                    is BusStopsItemData.BusStop -> item.busStopInfo
                    is BusStopsItemData.Header -> item.title
                }
            },
            errorKey = "bus-route-arrivals-error-key",
        ) { _, item ->
            when (item) {
                is BusStopsItemData.BusStop -> {
                    BusStopItem(
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                onBusStopClick(item.busStop)
                            }
                        },
                        data = item
                    )
                }
                is BusStopsItemData.Header -> {
                    Header(title = item.title)
                }
            }
        }
    }
}