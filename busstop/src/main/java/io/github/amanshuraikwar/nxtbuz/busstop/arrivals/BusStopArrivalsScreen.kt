package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

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
//    navController: NavController,
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
//    navController: NavController,
    onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    vm: BusStopArrivalsViewModel,
    busStopCode: String,
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
//        key = busStopCode,
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
        //key = busStopCode,
        modifier = modifier,
        state = bottomSheetState,
//        onInit = {
//            vm.bottomSheetInit = true
//        }
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
                            //data = screenState.header
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
//    navController: NavController,
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
                    is BusStopArrivalListItemData.BusStopArrival -> "${item.busServiceNumber}-arrival"
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
//                                navController.navigate(
//                                    "busRoute/${item.busServiceNumber}/${item.busStop.code}"
//                                )
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
//                is BusStopArrivalListItemData.BusStopHeader -> {
//                    BusStopHeaderItem(
//                        data = item
//                    )
//                }
            }
        }
    }
}