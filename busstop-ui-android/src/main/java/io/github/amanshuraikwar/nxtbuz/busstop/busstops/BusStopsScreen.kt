package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
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
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopsScreen(
    modifier: Modifier = Modifier,
    vm: BusStopsViewModel,
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
    onBusStopClick: (busStop: BusStop) -> Unit = {},
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        BottomSheetValue.Collapsed
    )

    val screenState by vm.screenState.collectAsState()

    LaunchedEffect(key1 = bottomSheetState.isInitialised) {
        if (bottomSheetState.isInitialised && screenState == BusStopsScreenState.Fetching) {
            vm.fetchBusStops()
        }
    }

    LaunchedEffect(key1 = showBottomSheet) {
        if (!showBottomSheet && screenState == BusStopsScreenState.Fetching) {
            vm.fetchBusStops()
        }
    }

    if (showBottomSheet) {
        NxtBuzBottomSheet(
            modifier = modifier,
            state = bottomSheetState,
            bottomSheetBgOffset = bottomSheetBgOffset
        ) { padding ->
            BusStopsView(
                state = screenState,
                padding = padding,
                onBusStopClick = onBusStopClick,
                onRetry = {
                    vm.fetchBusStops()
                },
                onUseDefaultLocation = {
                    vm.fetchBusStops(useDefaultLocation = true)
                }
            )
        }
    } else {
        Surface(
            modifier = modifier,
            elevation = 0.dp
        ) {
            BusStopsView(
                state = screenState,
                padding = PaddingValues(),
                onBusStopClick = onBusStopClick,
                onRetry = {
                    vm.fetchBusStops()
                },
                onUseDefaultLocation = {
                    vm.fetchBusStops(useDefaultLocation = true)
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BusStopsView(
    state: BusStopsScreenState,
    padding: PaddingValues,
    onBusStopClick: (busStop: BusStop) -> Unit = {},
    onRetry: () -> Unit = {},
    onUseDefaultLocation: () -> Unit = {}
) {
    when (state) {
        BusStopsScreenState.Failed -> TODO()
        BusStopsScreenState.Fetching -> {
            FetchingView(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = padding)
            )
        }
        is BusStopsScreenState.Success -> {
            if (state.listItems.isEmpty()) {
                NoBusStopsErrorView(
                    title = "There seem to be no bus stops near you :(",
                    primaryButtonText = "RETRY",
                    onPrimaryButtonClick = onRetry,
                    secondaryButtonText = "USE DEFAULT LOCATION",
                    onSecondaryButtonClick = onUseDefaultLocation
                )
            } else {
                NearbyBusStops(
                    state.listItems,
                    padding,
                    onBusStopClick
                )
            }
        }
        is BusStopsScreenState.LocationError -> {
            LocationErrorView(
                title = state.title,
                primaryButtonText = state.primaryButtonText,
                onPrimaryButtonClick = state.onPrimaryButtonClick,
                secondaryButtonText = state.secondaryButtonText,
                onSecondaryButtonClick = state.onSecondaryButtonClick
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun NearbyBusStops(
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
                    is BusStopsItemData.BusStop -> item.id
                    is BusStopsItemData.Header -> item.id
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