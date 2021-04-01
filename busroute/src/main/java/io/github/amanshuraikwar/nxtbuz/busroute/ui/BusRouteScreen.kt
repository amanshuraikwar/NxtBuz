package io.github.amanshuraikwar.nxtbuz.busroute.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.*
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet

@ExperimentalMaterialApi
@Composable
fun BusRouteScreen(
    modifier: Modifier = Modifier,
    busServiceNumber: String,
    busStopCode: String,
    vm: BusRouteViewModel
) {
    LaunchedEffect(key1 = busServiceNumber, key2 = busStopCode) {
        vm.init(busServiceNumber, busStopCode)
    }

    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    val lazyListState = rememberLazyListState()

    NxtBuzBottomSheet(
        modifier = modifier,
        bottomSheetState = bottomSheetState,
        lazyListState = lazyListState,
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
                        data = item,
                        onStarToggle = { newValue ->
                            vm.onStarToggle(
                                busServiceNumber = item.busServiceNumber,
                                busStopCode = item.busStopCode,
                                newValue = newValue,
                            )
                        }
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
}