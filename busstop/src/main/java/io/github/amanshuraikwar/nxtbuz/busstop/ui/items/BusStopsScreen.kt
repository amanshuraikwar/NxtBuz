package io.github.amanshuraikwar.nxtbuz.busstop.ui.items

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavController
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsItemData
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.common.compose.ComposeBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.Header
import io.github.amanshuraikwar.nxtbuz.common.compose.Puck

@ExperimentalMaterialApi
@Composable
fun BusStopsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    vm: BusStopsViewModel
) {
    val bottomSheetState = rememberBottomSheetState(
        BottomSheetValue.Collapsed
    )

    ComposeBottomSheet(
        modifier = modifier
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
                            is BusStopsItemData.BusStop -> item.busStopInfo
                            is BusStopsItemData.Header -> item.title
                        }
                    }
                ) { item ->
                    when (item) {
                        is BusStopsItemData.BusStop -> {
                            BusStopItem(data = item)
                        }
                        is BusStopsItemData.Header -> {
                            Header(title = item.title)
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 256.dp
    ) { }
}