package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.common.compose.*
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalItems(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: BusStopArrivalsViewModel,
    busStop: BusStop,
) {
    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = busStop.code) {
        vm.init(busStop)
    }

    NxtBuzBottomSheet(
        modifier = modifier,
        key = busStop.code,
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
                                // see: https://wajahatkarim.com/2021/03/pass-parcelable-compose-navigation/
                                navController.currentBackStackEntry?.arguments?.putParcelable(
                                    "busStop",
                                    item.busStop
                                )
                                navController.navigate(
                                    "busRoute/${item.busServiceNumber}"
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