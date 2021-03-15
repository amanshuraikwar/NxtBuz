package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalListItemData
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.bottomsheet.ComposeBottomSheet
import io.github.amanshuraikwar.nxtbuz.busstop.theme.outline

@ExperimentalMaterialApi
@Composable
fun BusStopArrivalItems(vm: BusStopArrivalsViewModel) {
    ComposeBottomSheet(
        backgroundColor = Color.Transparent,
        sheetContent = {
            val puckColor = MaterialTheme.colors.outline

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
            ) {
                drawRoundRect(
                    color = puckColor,
                    topLeft = Offset(center.x, size.height) - Offset(24.dp.toPx(), 4.dp.toPx()),
                    size = Size(48.dp.toPx(), 4.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 128.dp, top = 12.dp)
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