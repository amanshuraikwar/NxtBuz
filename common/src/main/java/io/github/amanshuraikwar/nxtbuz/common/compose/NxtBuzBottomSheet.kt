package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.LocalWindowInsets

@ExperimentalMaterialApi
@Composable
fun NxtBuzBottomSheet(
    modifier: Modifier = Modifier,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(
        BottomSheetValue.Collapsed
    ),
    lazyListState: LazyListState = rememberLazyListState(),
    bottomSheetContent: LazyListScope.() -> Unit
) {
    val insets = LocalWindowInsets.current
    val bottomSheetBgOffset = with(LocalDensity.current) { insets.statusBars.top.toDp() }

    ComposeBottomSheet(
        modifier = modifier,
        bottomSheetState = bottomSheetState,
        backgroundColor = Color.Transparent,
        bgOffset = bottomSheetBgOffset,
        sheetContent = {
            Box {
                Puck(
                    Modifier
                        .padding(top = bottomSheetBgOffset)
                        .alpha(
                            1 - bottomSheetState.expandProgressFraction
                        )
                )

                LazyColumn(
                    contentPadding = PaddingValues(
                        bottom = 128.dp,
                        top = bottomSheetBgOffset + 12.dp
                    ),
                    state = lazyListState,
                ) {
                    bottomSheetContent()
                }
            }
        },
        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp / 3).dp + bottomSheetBgOffset
    ) { }
}