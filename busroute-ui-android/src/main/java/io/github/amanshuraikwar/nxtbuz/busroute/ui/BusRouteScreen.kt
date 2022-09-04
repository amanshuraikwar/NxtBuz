package io.github.amanshuraikwar.nxtbuz.busroute.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRouteCurrentItem
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRouteNextItem
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRoutePreviousAllItem
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRoutePreviousItem
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.FailedView
import io.github.amanshuraikwar.nxtbuz.common.compose.FetchingView
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.expandProgressFraction
import io.github.amanshuraikwar.nxtbuz.common.compose.rememberNxtBuzBottomSheetState
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BusRouteScreen(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
    busStopCode: String,
    bottomSheetBgOffset: Dp,
    vm: BusRouteViewModel,
    showBottomSheet: Boolean,
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val screenState by vm.screenState.collectAsState(initial = BusRouteScreenState.Fetching)

    val backgroundColor = if (bottomSheetState.bottomSheetState.expandProgressFraction == 1f) {
        MaterialTheme.colors.surface
    } else {
        Color.Transparent
    }

    DisposableEffect(key1 = busServiceNumber, key2 = busStopCode) {
        vm.init(busServiceNumber, busStopCode)
        vm.bottomSheetInit = bottomSheetState.isInitialised
        onDispose {
            vm.onDispose()
        }
    }

    LaunchedEffect(key1 = bottomSheetState.isInitialised) {
        vm.bottomSheetInit = bottomSheetState.isInitialised
    }

    LaunchedEffect(key1 = busServiceNumber, key2 = busStopCode) {
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
            BusRouteArrivalsScreenStateView(
                busServiceNumber = busServiceNumber,
                screenState = screenState,
                padding = padding,
                backgroundColor = backgroundColor,
                onStarToggle = { newValue ->
                    vm.onStarToggle(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber,
                        newValue = newValue
                    )
                },
                onRetry = {
                    vm.init(
                        busServiceNumber = busServiceNumber,
                        busStopCode = busStopCode
                    )
                },
                onPreviousAllClicked = {
                    vm.previousAllClicked()
                },
                onExpand = {
                    vm.onExpand(expandingBusStopCode = it)
                },
                onCollapse = {
                    vm.onCollapse(collapsingBusStopCode = it)
                }
            )
        }
    } else {
        Surface(
            modifier = modifier,
            elevation = 0.dp
        ) {
            BusRouteArrivalsScreenStateView(
                busServiceNumber = busServiceNumber,
                screenState = screenState,
                padding = PaddingValues(),
                backgroundColor = backgroundColor,
                onStarToggle = { newValue ->
                    vm.onStarToggle(
                        busStopCode = busStopCode,
                        busServiceNumber = busServiceNumber,
                        newValue = newValue
                    )
                },
                onRetry = {
                    vm.init(
                        busServiceNumber = busServiceNumber,
                        busStopCode = busStopCode
                    )
                },
                onPreviousAllClicked = {
                    vm.previousAllClicked()
                },
                onExpand = {
                    vm.onExpand(expandingBusStopCode = it)
                },
                onCollapse = {
                    vm.onCollapse(collapsingBusStopCode = it)
                }
            )
        }
    }

}

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BusRouteArrivalsScreenStateView(
    busServiceNumber: String,
    screenState: BusRouteScreenState,
    padding: PaddingValues,
    backgroundColor: Color,
    onStarToggle: (newValue: Boolean) -> Unit,
    onRetry: () -> Unit,
    onPreviousAllClicked: () -> Unit,
    onExpand: (busStopCode: String) -> Unit,
    onCollapse: (busStopCode: String) -> Unit,
) {
    Crossfade(targetState = screenState) { state ->
        when (state) {
            is BusRouteScreenState.Failed -> {
                Column(
                    modifier = Modifier.padding(top = padding.calculateTopPadding())
                ) {
                    val header = state.header
                    if (header != null) {
                        BusRouteHeader(
                            modifier = Modifier
                                .background(color = backgroundColor),
                            data = header,
                            onStarToggle = onStarToggle
                        )
                    }

                    Divider()

                    FailedView(
                        onRetryClicked = onRetry
                    )
                }
            }
            BusRouteScreenState.Fetching -> {
                Column {
                    BusRouteHeader(
                        modifier = Modifier.padding(top = padding.calculateTopPadding()),
                        busServiceNumber = busServiceNumber
                    )

                    Divider()

                    FetchingView()
                }
            }
            is BusRouteScreenState.Success -> {
                Column {
                    BusRouteHeader(
                        modifier = Modifier
                            .background(color = backgroundColor)
                            .padding(top = padding.calculateTopPadding()),
                        data = state.header,
                        onStarToggle = onStarToggle
                    )

                    Divider()

                    CurrentBusStopArrivalsView(
                        data = state.currentBusStopArrivalsData.value
                    )

                    Divider()

                    if (state.listItems.isEmpty()) {
                        FetchingView()
                    } else {
                        BusRouteArrivalsView(
                            listItems = state.listItems,
                            onPreviousAllClicked = onPreviousAllClicked,
                            onExpand = onExpand,
                            onCollapse = onCollapse,
                        )
                    }
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BusRouteArrivalsView(
    listItems: List<BusRouteListItemData>,
    onPreviousAllClicked: () -> Unit,
    onExpand: (busStopCode: String) -> Unit,
    onCollapse: (busStopCode: String) -> Unit,
) {
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
                    is BusRouteListItemData.Header -> item.id
                    is BusRouteListItemData.BusRoutePreviousAll -> item.id
                    is BusRouteListItemData.BusRouteNode -> item.id
                }
            },
            errorKey = "bus-route-arrivals-error-key"
        ) { _, item ->

            when (item) {
                is BusRouteListItemData.Header -> {
                    Header(
                        title = item.title
                    )
                }
                is BusRouteListItemData.BusRoutePreviousAll -> {
                    BusRoutePreviousAllItem(
                        Modifier.clickable {
                            onPreviousAllClicked()
                        },
                        title = item.title
                    )
                }
                is BusRouteListItemData.BusRouteNode.Current -> {
                    BusRouteCurrentItem(
                        busStopDescription = item.busStopDescription,
                        position = item.position,
                        arrivalState = item.arrivalState,
                    )
                }
                is BusRouteListItemData.BusRouteNode.Previous -> {
                    BusRoutePreviousItem(
                        busStopDescription = item.busStopDescription,
                        position = item.position,
                        arrivalState = item.arrivalState,
                        onExpand = {
                            onExpand(item.busStopCode)
                        },
                        onCollapse = {
                            onCollapse(item.busStopCode)
                        }
                    )
                }
                is BusRouteListItemData.BusRouteNode.Next -> {
                    BusRouteNextItem(
                        busStopDescription = item.busStopDescription,
                        position = item.position,
                        arrivalState = item.arrivalState,
                        onExpand = {
                            onExpand(item.busStopCode)
                        },
                        onCollapse = {
                            onCollapse(item.busStopCode)
                        }
                    )
                }
            }
        }
    }
}