package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
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

    val backgroundColor by animateColorAsState(
        targetValue = if (
            bottomSheetState.progress.to == bottomSheetState.progress.from &&
            bottomSheetState.progress.to == BottomSheetValue.Expanded
        ) {
            MaterialTheme.colors.surface
        } else {
            Color.Transparent
        }
    )

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
            vm.startListeningArrivals()
        }
    ) { padding ->

        Crossfade(targetState = screenState) { screenState ->
            when (screenState) {
                BusStopArrivalsScreenState.Failed -> TODO()
                BusStopArrivalsScreenState.Fetching -> {
                    BusStopHeaderItem(
                        modifier = Modifier.padding(top = padding.calculateTopPadding())
                    )
                }
                is BusStopArrivalsScreenState.Success -> {
                    Column {
                        Surface(
                            color = backgroundColor,
                        ) {
                            BusStopHeaderItem(
                                modifier = Modifier.padding(top = padding.calculateTopPadding()),
                                data = screenState.header
                            )
                        }

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
    val lazyListState = rememberLazyListState()

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
                    is BusStopArrivalListItemData.BusStopArrival -> item.busServiceNumber
                    is BusStopArrivalListItemData.BusStopHeader -> item.busStopCode
                    is BusStopArrivalListItemData.Header -> item.title
                }
            },
            errorKey = ""
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