package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

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
    LaunchedEffect(key1 = busStop.code) {
        vm.init(busStop)
    }

    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    NxtBuzBottomSheet(
        modifier = modifier,
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
                                bottomSheetState.collapse()
                                navController.navigate(
                                    "busRoute/${item.busServiceNumber}"
                                )
                                lazyListState.scrollToItem(0)
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