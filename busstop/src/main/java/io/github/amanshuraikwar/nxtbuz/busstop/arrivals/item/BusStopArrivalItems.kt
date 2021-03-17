package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.common.compose.ComposeBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.Puck
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalItems(vm: BusStopArrivalsViewModel) {
    val bottomSheetState = rememberBottomSheetState(
        BottomSheetValue.Collapsed
    )
    val coroutineScope = rememberCoroutineScope()

    ComposeBottomSheet(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 16.dp),
        bottomSheetState = bottomSheetState,
        backgroundColor = Color.Transparent,
        sheetContent = {
            Puck()

            val lazyListState = rememberLazyListState()

            LazyColumn(
                contentPadding = PaddingValues(bottom = 128.dp, top = 12.dp),
                state = lazyListState,
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
                                        bottomSheetState.collapse()
                                        vm.onBusServiceClicked(item.busServiceNumber)
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
        }, sheetPeekHeight = 256.dp
    ) {

    }
}