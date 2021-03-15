package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.bottomsheet

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun ComposeBottomSheet(
    modifier: Modifier = Modifier,
    sheetContent: @Composable () -> Unit,
    sheetPeekHeight: Dp = BottomSheetScaffoldDefaults.SheetPeekHeight,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed),
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    body: @Composable () -> Unit
) {

    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(LocalDensity.current) { sheetPeekHeight.toPx() }

        ComposeBottomSheetStack(
            body = {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor,
                    contentColor = contentColor
                ) {
                    body()
                }
            },
            bottomSheet = {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .nestedScroll(bottomSheetState.PreUpPostDownNestedScrollConnection)
                        .swipeable(
                            state = bottomSheetState,
                            anchors = mapOf(
                                fullHeight - peekHeightPx to BottomSheetValue.Collapsed,
                                0f to BottomSheetValue.Expanded
                            ),
                            orientation = Orientation.Vertical,
                            enabled = true,
                            resistance = null
                        ),
                    shape = MaterialTheme.shapes.large.copy(
                        bottomEnd = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    ),
                    elevation = BottomSheetScaffoldDefaults.SheetElevation
                ) {
                    sheetContent()
                }
            },
            bottomSheetOffset = bottomSheetState.offset
        )
    }
}

@ExperimentalMaterialApi
val <T> SwipeableState<T>.PreUpPostDownNestedScrollConnection: NestedScrollConnection
    get() = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.toFloat()
            return if (delta < 0 && source == NestedScrollSource.Drag) {
                performDrag(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return if (source == NestedScrollSource.Drag) {
                performDrag(available.toFloat()).toOffset()
            } else {
                Offset.Zero
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val toFling = Offset(available.x, available.y).toFloat()
            return if (toFling < 0 && offset.value > 0.0) {
                performFling(velocity = toFling)
                // since we go to the anchor with tween settling, consume all for the best UX
                available
            } else {
                Velocity.Zero
            }
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            performFling(velocity = Offset(available.x, available.y).toFloat())
            return available
        }

        private fun Float.toOffset(): Offset = Offset(0f, this)

        private fun Offset.toFloat(): Float = this.y
    }

@Composable
private fun ComposeBottomSheetStack(
    body: @Composable () -> Unit,
    bottomSheet: @Composable () -> Unit,
    bottomSheetOffset: State<Float>
) {
    Layout(
        content = {
            body()
            bottomSheet()
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)

            val sheetPlaceable =
                measurables[1].measure(constraints.copy(minWidth = 0, minHeight = 0))

            val sheetOffsetY = bottomSheetOffset.value.roundToInt()

            sheetPlaceable.placeRelative(0, sheetOffsetY)
        }
    }
}