package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopHeaderItem
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model.BusStopArrivalListItemData
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
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = busStopCode) {
        vm.init(busStopCode)
    }

    NxtBuzBottomSheet(
        modifier = modifier,
        key = busStopCode,
        bottomSheetState = bottomSheetState,
        lazyListState = lazyListState,
    ) {
        items(
            items = vm.listItems,
            key = { item ->
                when (item) {
                    is BusStopArrivalListItemData.BusStopArrival -> item.busServiceNumber
                    is BusStopArrivalListItemData.BusStopHeader -> item.busStopCode
                    is BusStopArrivalListItemData.Header -> item.title
                }
            }
        ) { item ->
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
                        data = item
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