package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.busroute.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteViewModel
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.ComposeBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.Puck

@ExperimentalMaterialApi
@Composable
fun BusRouteItems(vm: BusRouteViewModel) {
    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)

    ComposeBottomSheet(
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
                            is BusRouteListItemData.BusRouteHeader -> item.busServiceNumber
                            is BusRouteListItemData.Header -> item.title
                            is BusRouteListItemData.BusRoutePreviousAll -> item.title
                            is BusRouteListItemData.BusRouteNode -> item.busStopDescription
                        }
                    }
                ) { item ->
                    when (item) {
                        is BusRouteListItemData.Header -> {
                            Header(
                                title = item.title
                            )
                        }
                        is BusRouteListItemData.BusRouteHeader -> {
                            BusRouteHeaderItem(
                                data = item
                            )
                        }
                        is BusRouteListItemData.BusRoutePreviousAll -> {
                            BusRoutePreviousAllItem(
                                Modifier.clickable {
                                    vm.previousAllClicked()
                                },
                                title = item.title
                            )
                        }
                        is BusRouteListItemData.BusRouteNode.Current -> {
                            BusRouteCurrentItem(
                                busStopDescription = item.busStopDescription,
                                position = item.position,
                                arrivalState = item.arrivalState,
                            )
                        }
                        is BusRouteListItemData.BusRouteNode.Previous -> {
                            BusRoutePreviousItem(
                                busStopDescription = item.busStopDescription,
                                position = item.position,
                                arrivalState = item.arrivalState,
                                onExpand = {
                                    vm.onExpand(item.busStopCode)
                                },
                                onCollapse = {
                                    vm.onCollapse(item.busStopCode)
                                }
                            )
                        }
                        is BusRouteListItemData.BusRouteNode.Next -> {
                            BusRouteNextItem(
                                busStopDescription = item.busStopDescription,
                                position = item.position,
                                arrivalState = item.arrivalState,
                                onExpand = {
                                   vm.onExpand(item.busStopCode)
                                },
                                onCollapse = {
                                    vm.onCollapse(item.busStopCode)
                                }
                            )
                        }
                    }
                }
            }
        }, sheetPeekHeight = 256.dp
    ) {

    }
}