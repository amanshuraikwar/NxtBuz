package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalItems(vm: BusStopArrivalsViewModel) {

    ComposeBottomSheet(
        backgroundColor = Color.Transparent,
        sheetContent = {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 128.dp)
            ) {
                items(
                    items = vm.listItems,
//                    key = {
//                        it.busServiceNumber
//                    }
                ) { item ->
                    when (item) {
                        is BusStopArrivalListItemData.BusStopArrival -> {
                            BusStopArrivalItem(
                                data = item
                            )
                        }
                        is BusStopArrivalListItemData.Header -> {
                            Header(
                                title = item.title
                            )
                        }
                    }
                }
            }
        }, sheetPeekHeight = 256.dp
    ) {

    }
}