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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalsScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.expandProgressFraction
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: BusStopArrivalsViewModel,
    busStop: BusStop,
) {
    BusStopArrivalsScreen(
        modifier,
        navController,
        vm,
        busStop.code
    )
}

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: BusStopArrivalsViewModel,
    busStopCode: String,
) {
    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val screenState by vm.screenState.collectAsState(initial = BusStopArrivalsScreenState.Fetching)

    val backgroundColor = if (bottomSheetState.expandProgressFraction == 1f) {
        MaterialTheme.colors.surface
    } else {
        Color.Transparent
    }

    DisposableEffect(key1 = busStopCode) {
        vm.init(busStopCode)
        onDispose {
            vm.onDispose()
        }
    }

    NxtBuzBottomSheet(
        modifier = modifier,
        bottomSheetState = bottomSheetState,
        onInit = {
            vm.bottomSheetInit = true
        }
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
                                data = header
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
                            data = screenState.header
                        )

                        Divider()

                        if (screenState.listItems.isEmpty()) {
                            FetchingView()
                        } else {
                            BusStopArrivalsView(
                                listItems = screenState.listItems,
                                navController = navController,
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

inline fun <T, K : Any> LazyListScope.itemsIndexed(
    items: List<T>,
    noinline key: ((index: Int, item: T) -> K),
    errorKey: K,
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) = items(
    items.size,
    { index: Int ->
        if (index < items.size) {
            key(index, items[index])
        } else {
            errorKey
        }
    }
) {
    if (it < items.size) {
        itemContent(it, items[it])
    }
}

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalsView(
    listItems: List<BusStopArrivalListItemData>,
    navController: NavController,
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
        itemsIndexed(
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
                                navController.navigate(
                                    "busRoute/${item.busServiceNumber}/${item.busStop.code}"
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
                        data = item
                    )
                }
            }
        }
    }
}