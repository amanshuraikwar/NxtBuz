package io.github.amanshuraikwar.nxtbuz.common.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.largeShapeSizeDp
import kotlin.math.roundToInt

@ExperimentalMaterialApi
val BottomSheetState.expandProgressFraction: Float
    get() {
        return try {
            when (progress.to) {
                progress.from -> {
                    if (progress.from == BottomSheetValue.Collapsed) {
                        0f
                    } else {
                        1f
                    }
                }
                BottomSheetValue.Collapsed -> {
                    1f - progress.fraction
                }
                BottomSheetValue.Expanded -> {
                    progress.fraction
                }
                else -> 0f
            }
        } catch (e: Exception) {
            1f
        }
    }

@ExperimentalMaterialApi
@Composable
fun ComposeBottomSheet(
    modifier: Modifier = Modifier,
    sheetContent: @Composable () -> Unit,
    sheetPeekHeight: Dp = BottomSheetScaffoldDefaults.SheetPeekHeight,
    bottomSheetState: BottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed),
    bgOffset: Dp = 0.dp,
    body: @Composable () -> Unit
) {
    val surfaceColor = MaterialTheme.colors.surface
    val cornerRadius = MaterialTheme.shapes.largeShapeSizeDp

    val yOffset = if (bottomSheetState.expandProgressFraction < 0.8) {
        bgOffset
    } else {
        (bgOffset * (1 - bottomSheetState.expandProgressFraction)) * 5
    }

    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(LocalDensity.current) { sheetPeekHeight.toPx() }

        ComposeBottomSheetStack(
            body = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    body()
                }
            },
            bottomSheet = {
                Layout(
                    modifier = Modifier
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
                    content = {
                        Canvas(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            drawRoundRect(
                                color = surfaceColor,
                                topLeft = Offset(
                                    0f,
                                    yOffset.toPx()
                                ),
                                size = Size(
                                    size.width,
                                    size.height - yOffset.toPx()
                                ),
                                cornerRadius = CornerRadius(
                                    cornerRadius.toPx()
                                            * (1 - bottomSheetState.expandProgressFraction)
                                )
                            )
                        }

                        Column {
                            sheetContent()
                        }
                    }
                ) { measurables, constraints ->
                    val contentPlaceable = measurables[1].measure(
                        constraints.copy(
                            minWidth = constraints.maxWidth,
                            maxWidth = constraints.maxWidth,
                            minHeight = constraints.maxHeight,
                            maxHeight = constraints.maxHeight,
                        )
                    )

                    val bgPlaceable = measurables[0].measure(
                        constraints.copy(
                            minWidth = contentPlaceable.width,
                            maxWidth = contentPlaceable.width,
                            minHeight = contentPlaceable.height,
                            maxHeight = contentPlaceable.height,
                        )
                    )

                    layout(
                        width = contentPlaceable.width,
                        height = contentPlaceable.height
                    ) {
                        bgPlaceable.place(0, 0)
                        contentPlaceable.place(0, 0)
                    }
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