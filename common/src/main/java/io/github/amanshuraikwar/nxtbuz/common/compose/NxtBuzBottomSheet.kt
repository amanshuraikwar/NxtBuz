package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.LocalWindowInsets

@ExperimentalMaterialApi
@Composable
fun /*<T>*/ rememberNxtBuzBottomSheetState(
//    key: T? = null,
    initialValue: BottomSheetValue,
): NxtBuzBottomSheetState {
    return remember(/*key*/) {
        NxtBuzBottomSheetState(initialValue = initialValue)
    }
}

@ExperimentalMaterialApi
class NxtBuzBottomSheetState constructor(
    initialValue: BottomSheetValue,
    val bottomSheetState: BottomSheetState = BottomSheetState(initialValue)
) {
    var isInitialised: Boolean by mutableStateOf(false)
        internal set
}

@ExperimentalMaterialApi
@Composable
fun NxtBuzBottomSheet(
    modifier: Modifier = Modifier,
    key: String? = null,
    state: NxtBuzBottomSheetState = remember {
        NxtBuzBottomSheetState(BottomSheetValue.Collapsed)
    },
//        rememberBottomSheetState(
//            BottomSheetValue.Collapsed
//        ),
    //onInit: () -> Unit = {},
    bottomSheetContent: @Composable (PaddingValues) -> Unit,
) {
    val insets = LocalWindowInsets.current
    val bottomSheetBgOffset = with(LocalDensity.current) { insets.statusBars.top.toDp() }

    var alpha by remember(key) {
        mutableStateOf(0f)
    }
    var offsetY by remember(key) {
        mutableStateOf(128.dp)
    }

    LaunchedEffect(key) {
        state.isInitialised = false
        if (state.bottomSheetState.isExpanded) {
            state.bottomSheetState.collapse()
        }
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300)
        ) { animatedValue, _ ->
            alpha = animatedValue
            offsetY = ((1 - animatedValue) * 128).dp
        }
        state.isInitialised = true
        //onInit()
    }

    ComposeBottomSheet(
        modifier = modifier
            .alpha(alpha = alpha)
            .offset(y = offsetY),
        bottomSheetState = state.bottomSheetState,
        backgroundColor = Color.Transparent,
        bgOffset = bottomSheetBgOffset,
        sheetContent = {
            Box {
                Puck(
                    Modifier
                        .padding(top = bottomSheetBgOffset)
                        .alpha(
                            1 - state.bottomSheetState.expandProgressFraction
                        )
                )

                bottomSheetContent(
                    PaddingValues(top = bottomSheetBgOffset + 12.dp)
                )
            }
        },
        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp / 3).dp + bottomSheetBgOffset
    ) { }
}

//@ExperimentalMaterialApi
//@Composable
//fun NxtBuzBottomSheet(
//    modifier: Modifier = Modifier,
//    key: String? = null,
//    bottomSheetState: BottomSheetState = rememberBottomSheetState(
//        BottomSheetValue.Collapsed
//    ),
//    lazyListState: LazyListState = rememberLazyListState(),
//    bottomSheetContent: LazyListScope.() -> Unit,
//) {
//    val insets = LocalWindowInsets.current
//    val bottomSheetBgOffset = with(LocalDensity.current) { insets.statusBars.top.toDp() }
//
//    var alpha by remember(key) {
//        mutableStateOf(0f)
//    }
//    var offsetY by remember(key) {
//        mutableStateOf(128.dp)
//    }
//
//    LaunchedEffect(key) {
//        bottomSheetState.collapse()
//        lazyListState.scrollToItem(0)
//        animate(
//            initialValue = 0f,
//            targetValue = 1f,
//            animationSpec = tween(300)
//        ) { animatedValue, _ ->
//            alpha = animatedValue
//            offsetY = ((1 - animatedValue) * 128).dp
//        }
//    }
//
//    ComposeBottomSheet(
//        modifier = modifier
//            .alpha(alpha = alpha)
//            .offset(y = offsetY),
//        bottomSheetState = bottomSheetState,
//        backgroundColor = Color.Transparent,
//        bgOffset = bottomSheetBgOffset,
//        sheetContent = {
//            Box {
//                Puck(
//                    Modifier
//                        .padding(top = bottomSheetBgOffset)
//                        .alpha(
//                            1 - bottomSheetState.expandProgressFraction
//                        )
//                )
//
//                LazyColumn(
//                    contentPadding = PaddingValues(
//                        bottom = 128.dp,
//                        top = bottomSheetBgOffset + 12.dp
//                    ),
//                    state = lazyListState,
//                ) {
//                    bottomSheetContent()
//                }
//            }
//        },
//        sheetPeekHeight = (LocalConfiguration.current.screenHeightDp / 3).dp + bottomSheetBgOffset
//    ) { }
//}