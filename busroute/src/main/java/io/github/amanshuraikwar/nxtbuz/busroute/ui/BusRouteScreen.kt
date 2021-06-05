package io.github.amanshuraikwar.nxtbuz.busroute.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.*
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.*
import io.github.amanshuraikwar.nxtbuz.common.compose.util.itemsIndexedSafe

@ExperimentalMaterialApi
@Composable
fun BusRouteScreen(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
    busStopCode: String,
    vm: BusRouteViewModel
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
//        key = "$busStopCode$busServiceNumber",
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
//        if (bottomSheetState.isCollapsed) {
//            vm.bottomSheetInit = true
//        }
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
//        onInit = {
//            vm.bottomSheetInit = true
//        }
    ) { padding ->
        Crossfade(targetState = screenState) { screenState ->
            when (screenState) {
                is BusRouteScreenState.Failed -> {
                    Column(
                        modifier = Modifier.padding(top = padding.calculateTopPadding())
                    ) {
                        val header = screenState.header
                        if (header != null) {
                            BusRouteHeader(
                                modifier = Modifier
                                    .background(color = backgroundColor),
                                data = header,
                                onStarToggle = { newValue ->
                                    vm.onStarToggle(
                                        busServiceNumber = header.busServiceNumber,
                                        busStopCode = header.busStopCode,
                                        newValue = newValue,
                                    )
                                }
                            )
                        }

                        Divider()

                        FailedView(
                            onRetryClicked = {
                                vm.init(busServiceNumber, busStopCode)
                            }
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
                            data = screenState.header,
                            onStarToggle = { newValue ->
                                vm.onStarToggle(
                                    busServiceNumber = screenState.header.busServiceNumber,
                                    busStopCode = screenState.header.busStopCode,
                                    newValue = newValue,
                                )
                            }
                        )

                        Divider()

                        if (screenState.listItems.isEmpty()) {
                            FetchingView()
                        } else {
                            BusRouteViewArrivalsView(
                                listItems = screenState.listItems,
                                onPreviousAllClicked = {
                                    vm.previousAllClicked()
                                },
                                onExpand = {
                                    vm.onExpand(it)
                                },
                                onCollapse = {
                                    vm.onCollapse(it)
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
fun BusRouteViewArrivalsView(
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
                    is BusRouteListItemData.Header -> item.title
                    is BusRouteListItemData.BusRoutePreviousAll -> item.title
                    is BusRouteListItemData.BusRouteNode -> item.busStopDescription
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