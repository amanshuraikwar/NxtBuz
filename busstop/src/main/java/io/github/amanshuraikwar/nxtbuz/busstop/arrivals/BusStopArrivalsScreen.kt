package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalsScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.*
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsScreen(
    modifier: Modifier = Modifier,
    vm: BusStopArrivalsViewModel,
    busStop: BusStop,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit = { _, _ -> },
) {
    BusStopArrivalsScreen(
        modifier,
        onBusServiceClick,
        vm,
        busStop.code
    )
}

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsScreen(
    modifier: Modifier = Modifier,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    vm: BusStopArrivalsViewModel,
    busStopCode: String,
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val screenState by vm.screenState.collectAsState()

    val backgroundColor = if (bottomSheetState.bottomSheetState.expandProgressFraction == 1f) {
        MaterialTheme.colors.surface
    } else {
        Color.Transparent
    }

    DisposableEffect(key1 = busStopCode) {
        vm.bottomSheetInit = bottomSheetState.isInitialised
        vm.init(busStopCode)
        onDispose {
            vm.onDispose()
        }
    }

    LaunchedEffect(key1 = bottomSheetState.isInitialised) {
        vm.bottomSheetInit = bottomSheetState.isInitialised
    }

    NxtBuzBottomSheet(
        modifier = modifier,
        state = bottomSheetState,
    ) { padding ->
        Crossfade(targetState = screenState) { screenState ->
            when (screenState) {
                is BusStopArrivalsScreenState.Failed -> {
                    Column(
                        modifier = Modifier.padding(top = padding.calculateTopPadding())
                    ) {
                        val header = screenState.header
                        if (header != null) {
                            BusStopHeaderItem(
                                modifier = Modifier
                                    .background(color = backgroundColor),
                                busStopDescription = header.busStopDescription,
                                busStopRoadName = header.busStopRoadName,
                                busStopCode = header.busStopCode,
                            )
                        }

                        Divider()

                        FailedView(
                            onRetryClicked = {
                                vm.init(busStopCode = busStopCode)
                            }
                        )
                    }
                }
                BusStopArrivalsScreenState.Fetching -> {
                    Column {
                        BusStopHeaderItem(
                            modifier = Modifier.padding(top = padding.calculateTopPadding()),
                        )

                        Divider()

                        FetchingView()
                    }
                }
                is BusStopArrivalsScreenState.Success -> {
                    Column {
                        BusStopHeaderItem(
                            modifier = Modifier
                                .background(color = backgroundColor)
                                .padding(top = padding.calculateTopPadding()),
                            busStopDescription = screenState.header.busStopDescription,
                            busStopRoadName = screenState.header.busStopRoadName,
                            busStopCode = screenState.header.busStopCode,
                        )

                        Divider()

                        if (screenState.listItems.isEmpty()) {
                            FetchingView()
                        } else {
                            BusStopArrivalsView(
                                listItems = screenState.listItems,
                                onBusServiceClick = onBusServiceClick,
                                onStarToggle = { busServiceNumber, newToggleState ->
                                    vm.onStarToggle(
                                        newToggleState,
                                        busServiceNumber
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsView(
    listItems: List<BusStopArrivalListItemData>,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    onStarToggle: (busServiceNumber: String, newToggleState: Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = remember {
        LazyListState(
            0,
            0
        )
    }

    LaunchedEffect(null) {
        lazyListState.scrollToItem(0)
    }

    LazyColumn(
        contentPadding = PaddingValues(
            bottom = 256.dp,
        ),
        state = lazyListState,
    ) {
        itemsIndexedSafe(
            items = listItems,
            key = { _, item ->
                when (item) {
                    is BusStopArrivalListItemData.BusStopArrival ->
                        "${item.busServiceNumber}-arrival"
                    is BusStopArrivalListItemData.BusStopHeader -> item.busStopCode
                    is BusStopArrivalListItemData.Header -> item.title
                }
            },
            errorKey = "bus-stop-arrivals-error-key"
        ) { _, item ->
            when (item) {
                is BusStopArrivalListItemData.BusStopArrival -> {
                    BusStopArrivalItem(
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                onBusServiceClick(
                                    item.busStop.code,
                                    item.busServiceNumber
                                )
                            }
                        },
                        data = item,
                        onStarToggle = {
                            onStarToggle(
                                item.busServiceNumber, it
                            )
                        }
                    )
                }
                is BusStopArrivalListItemData.Header -> {
                    Header(
                        title = item.title
                    )
                }
                is BusStopArrivalListItemData.BusStopHeader -> {
                    BusStopHeaderItem(
                        busStopDescription = item.busStopDescription,
                        busStopRoadName = item.busStopRoadName,
                        busStopCode = item.busStopCode,
                    )
                }
            }
        }
    }
}