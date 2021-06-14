package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit = { _, _ -> },
) {
    BusStopArrivalsScreen(
        modifier,
        vm,
        busStop.code,
        bottomSheetBgOffset,
        showBottomSheet,
        onBusServiceClick
    )
}

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsScreen(
    modifier: Modifier = Modifier,
    vm: BusStopArrivalsViewModel,
    busStopCode: String,
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
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

    LaunchedEffect(key1 = busStopCode) {
        if (!showBottomSheet) {
            vm.bottomSheetInit = true
        }
    }

    if (showBottomSheet) {
        NxtBuzBottomSheet(
            modifier = modifier,
            state = bottomSheetState,
            bottomSheetBgOffset = bottomSheetBgOffset
        ) { padding ->
            BusStopArrivalsScreenStateView(
                screenState = screenState,
                padding = padding,
                backgroundColor = backgroundColor,
                onRetryClick = {
                    vm.init(busStopCode)
                },
                onBusServiceClick = onBusServiceClick,
                onStarToggle = { busServiceNumber, newToggleState ->
                    vm.onStarToggle(
                        busServiceNumber = busServiceNumber,
                        newToggleState = newToggleState,
                    )
                },
                onGoToBusStopClicked = {
                    vm.onGoToBusStopClicked()
                }
            )
        }
    } else {
        Surface(
            modifier = modifier,
            elevation = 0.dp
        ) {
            BusStopArrivalsScreenStateView(
                screenState = screenState,
                padding = PaddingValues(),
                backgroundColor = backgroundColor,
                onRetryClick = {
                    vm.init(busStopCode)
                },
                onBusServiceClick = onBusServiceClick,
                onStarToggle = { busServiceNumber, newToggleState ->
                    vm.onStarToggle(
                        busServiceNumber = busServiceNumber,
                        newToggleState = newToggleState,
                    )
                },
                onGoToBusStopClicked = {
                    vm.onGoToBusStopClicked()
                }
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsScreenStateView(
    screenState: BusStopArrivalsScreenState,
    padding: PaddingValues = PaddingValues(),
    backgroundColor: Color,
    onRetryClick: () -> Unit,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    onStarToggle: (busServiceNumber: String, newToggleState: Boolean) -> Unit,
    onGoToBusStopClicked: () -> Unit,
) {
    Crossfade(targetState = screenState) { state ->
        when (state) {
            is BusStopArrivalsScreenState.Failed -> {
                Column(
                    modifier = Modifier.padding(top = padding.calculateTopPadding())
                ) {
                    val header = state.header
                    if (header != null) {
                        BusStopHeaderItem(
                            modifier = Modifier
                                .background(color = backgroundColor),
                            busStopDescription = header.busStopDescription,
                            busStopRoadName = header.busStopRoadName,
                            busStopCode = header.busStopCode,
                            onGoToBusStopClicked = onGoToBusStopClicked
                        )
                    }

                    Divider()

                    FailedView(
                        onRetryClicked = {
                            onRetryClick()
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
                        busStopDescription = state.header.busStopDescription,
                        busStopRoadName = state.header.busStopRoadName,
                        busStopCode = state.header.busStopCode,
                        onGoToBusStopClicked = onGoToBusStopClicked
                    )

                    Divider()

                    Crossfade(targetState = state.listItems) { listItems ->
                        if (listItems.isEmpty()) {
                            FetchingView()
                        } else {
                            BusStopArrivalsView(
                                listItems = listItems,
                                onBusServiceClick = onBusServiceClick,
                                onStarToggle = onStarToggle,
                                onGoToBusStopClicked = onGoToBusStopClicked
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
    onStarToggle: (busServiceNumber: String, newToggleState: Boolean) -> Unit,
    onGoToBusStopClicked: () -> Unit,
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
                        onGoToBusStopClicked = onGoToBusStopClicked,
                    )
                }
            }
        }
    }
}