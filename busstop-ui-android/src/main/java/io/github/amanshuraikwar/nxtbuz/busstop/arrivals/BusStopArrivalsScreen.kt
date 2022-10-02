package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalsScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.FailedView
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderView
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.expandProgressFraction
import io.github.amanshuraikwar.nxtbuz.common.compose.rememberNxtBuzBottomSheetState
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
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

@ExperimentalAnimationApi
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
                },
                onBusStopStarToggle = vm::onBusStopStarToggle
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
                },
                onBusStopStarToggle = vm::onBusStopStarToggle
            )
        }
    }
}

@ExperimentalAnimationApi
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
    onBusStopStarToggle: (busStopCode: String, newStarState: Boolean) -> Unit,
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
                            onGoToBusStopClicked = onGoToBusStopClicked,
                            starred = header.starred,
                            onStarToggle = {
                                onBusStopStarToggle(header.busStopCode, it)
                            }
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
                        onGoToBusStopClicked = onGoToBusStopClicked,
                        starred = state.header.starred,
                        onStarToggle = {
                            onBusStopStarToggle(state.header.busStopCode, it)
                        }
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
                                onGoToBusStopClicked = onGoToBusStopClicked,
                                onBusStopStarToggle = onBusStopStarToggle
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsView(
    listItems: List<BusStopArrivalListItemData>,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    onStarToggle: (busServiceNumber: String, newToggleState: Boolean) -> Unit,
    onGoToBusStopClicked: () -> Unit,
    onBusStopStarToggle: (busStopCode: String, newStarState: Boolean) -> Unit,
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
                    is BusStopArrivalListItemData.BusStopArrival -> item.id
                    is BusStopArrivalListItemData.BusStopHeader -> item.id
                    is BusStopArrivalListItemData.Header -> item.id
                }
            },
            errorKey = "bus-stop-arrivals-error-key"
        ) { _, item ->
            when (item) {
                is BusStopArrivalListItemData.BusStopArrival -> {
                    BusStopArrivalItem(
                        modifier = Modifier,
                        data = item,
                        onStarToggle = {
                            onStarToggle(
                                item.busServiceNumber, it
                            )
                        },
                        onClick = {
                            coroutineScope.launch {
                                onBusServiceClick(
                                    item.busStop.code,
                                    item.busServiceNumber
                                )
                            }
                        }
                    )
                }
                is BusStopArrivalListItemData.Header -> {
                    HeaderView(
                        title = item.title
                    )
                }
                is BusStopArrivalListItemData.BusStopHeader -> {
                    BusStopHeaderItem(
                        busStopDescription = item.busStopDescription,
                        busStopRoadName = item.busStopRoadName,
                        busStopCode = item.busStopCode,
                        onGoToBusStopClicked = onGoToBusStopClicked,
                        starred = item.starred,
                        onStarToggle = {
                            onBusStopStarToggle(item.busStopCode, it)
                        }
                    )
                }
            }
        }
    }
}