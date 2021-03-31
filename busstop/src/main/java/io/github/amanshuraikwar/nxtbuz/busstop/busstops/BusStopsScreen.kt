package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.items.BusStopItem
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: BusStopsViewModel
) {
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
                    is BusStopsItemData.BusStop -> item.busStopInfo
                    is BusStopsItemData.Header -> item.title
                }
            }
        ) { item ->
            when (item) {
                is BusStopsItemData.BusStop -> {
                    BusStopItem(
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                // see: https://wajahatkarim.com/2021/03/pass-parcelable-compose-navigation/
                                navController.currentBackStackEntry?.arguments?.putParcelable(
                                    "busStop",
                                    item.busStop
                                )
                                navController.navigate("busStopArrival")
                            }
                        },
                        data = item
                    )
                }
                is BusStopsItemData.Header -> {
                    Header(title = item.title)
                }
            }
        }
    }
}