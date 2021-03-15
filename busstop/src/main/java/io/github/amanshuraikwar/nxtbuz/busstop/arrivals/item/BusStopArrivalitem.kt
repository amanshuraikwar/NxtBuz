package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import android.util.Log
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.theme.star
import io.github.amanshuraikwar.nxtbuz.busstop.theme.white
import io.github.amanshuraikwar.nxtbuz.busstop.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BusStopArrivalItems(vm: ComposeTestViewModel = viewModel()) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

//    Layout(
//        modifier = Modifier
//            .background(color = white)
//            .fillMaxHeight()
//            .fillMaxWidth(),
//        content = {
//            Text("hello helo helo heloheloheloheloheloheohkeo")
//            Surface(
//                color = Color.Black
//            ) {
//                Text("world")
//            }
//        }
//    ) { measurables, constraints ->
//        val placeables = measurables.map { it.measure(constraints.copy(minHeight = 0)) }
//
//        layout(placeables.maxOf { it.width }, placeables.sumBy { it.height }) {
//            var y = 100
//
//            placeables.forEach {
//                Log.d("yoyo", "$y")
//                it.placeRelative(0, y)
//                y += it.height
//            }
//        }
//    }

    ComposeBottomSheet(
//        backgroundColor = Color.Transparent,
//        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            LazyColumn {
                items(vm.list) { item ->
                    BusStopArrivalItem(
                        data = item
                    )
                }
            }
        }, sheetPeekHeight = 200.dp
    ) {

    }
}

@ExperimentalMaterialApi
@Composable
fun ComposeBottomSheet(
    sheetContent: @Composable () -> Unit,
    sheetPeekHeight: Dp,
    body: @Composable () -> Unit = {
        Surface(
            color = Color.Transparent
        ) {
            Column(Modifier.fillMaxSize()) {

            }
        }
    }
) {
    val bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)

    BoxWithConstraints(/*modifier*/) {
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(LocalDensity.current) { sheetPeekHeight.toPx() }
        var bottomSheetHeight by remember { mutableStateOf(fullHeight) }

        ComposeBottomSheetStack(
            body = {
                Box {
                    body()
                }
            },
            bottomSheet = {
                Box(
                    Modifier
                        .nestedScroll(bottomSheetState.PreUpPostDownNestedScrollConnection)
                        .swipeable(
                            state = bottomSheetState,
                            anchors = mapOf(
                                fullHeight - peekHeightPx to BottomSheetValue.Collapsed,
                                fullHeight - bottomSheetHeight to BottomSheetValue.Expanded
                            ),
                            orientation = Orientation.Vertical,
                            enabled = true,
                            resistance = null
                        ).background(color = MaterialTheme.colors.surface)
                ) {
                    sheetContent()
                }
            },
            bottomSheetOffset = bottomSheetState.offset
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
            return if (toFling < 0 && offset.value > Float.NEGATIVE_INFINITY) {
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
        Log.d("yoyo", "ComposeBottomSheetStack: ${measurables.size}")
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)

            val (sheetPlaceable) = measurables.drop(1).map {
                it.measure(constraints.copy(minWidth = 0, minHeight = 0))
            }

            val sheetOffsetY = bottomSheetOffset.value.roundToInt()

            sheetPlaceable.placeRelative(0, sheetOffsetY)
        }
    }
}

@Composable
fun BusStopArrivalItem(
    modifier: Modifier = Modifier,
    data: Data
) {
    var alpha by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(data.busType) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(300, delayMillis = 300)
        ) { animatedValue, _ ->
            alpha = animatedValue
        }
    }

    Box(
        modifier = modifier.alpha(alpha),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
        ) {
            BusService(
                busServiceNumber = data.busServiceNumber,
                busType = data.busType
            )

            Column(
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            ) {
                BusArrival(
                    arrival = data.arrival,
                    busLoad = data.busLoad,
                    wheelchairAccess = data.wheelchairAccess
                )

                Spacer(modifier = Modifier.size(2.dp))

                BusDestination(
                    destinationBusStopDescription = data.destinationBusStopDescription
                )
            }
        }

        Icon(
            imageVector = Icons.Rounded.StarBorder,
            contentDescription = "Star",
            tint = MaterialTheme.colors.star,
            modifier = Modifier
                .clip(shape = CircleShape)
                .clickable {

                }
                .padding(16.dp)
        )
    }
}

@Composable
fun BusService(
    busServiceNumber: String,
    busType: BusType,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                when (busType) {
                    BusType.SD -> R.drawable.ic_bus_normal_16
                    BusType.DD -> R.drawable.ic_bus_dd_16
                    BusType.BD -> R.drawable.ic_bus_feeder_16
                }
            ),
            modifier = Modifier.size(16.dp),
            contentDescription = "Bus Type",
            tint = MaterialTheme.colors.onSurface
        )

        Text(
            text = busServiceNumber,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Normal),
            color = MaterialTheme.colors.onPrimary,
            modifier = Modifier
                .background(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colors.primary
                )
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}

@Composable
fun BusDestination(
    destinationBusStopDescription: String,
) {
    Row {
        Icon(
            painter = painterResource(R.drawable.ic_destination_16),
            modifier = Modifier.size(16.dp),
            contentDescription = "Wheelchair Access",
            tint = MaterialTheme.colors.onSurface
        )

        Text(
            text = destinationBusStopDescription,
            style = MaterialTheme.typography.overline,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

@Composable
fun BusArrival(
    arrival: String,
    busLoad: BusLoad,
    wheelchairAccess: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = arrival,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colors.onSurface,
        )

        Spacer(modifier = Modifier.size(16.dp))

        Icon(
            painter = painterResource(
                when (busLoad) {
                    BusLoad.SEA -> R.drawable.ic_bus_load_1_16
                    BusLoad.SDA -> R.drawable.ic_bus_load_2_16
                    BusLoad.LSD -> R.drawable.ic_bus_load_3_16
                }
            ),
            modifier = Modifier.size(16.dp),
            contentDescription = "Bus Load",
            tint = MaterialTheme.colors.onSurface
        )

        Icon(
            painter = painterResource(
                if (wheelchairAccess) {
                    R.drawable.ic_accessible_16
                } else {
                    R.drawable.ic_not_accessible_16
                }
            ),
            modifier = Modifier.size(18.dp),
            contentDescription = "Wheelchair Access",
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Preview
@Composable
fun BusStopArrivalItemPreview() {
    PreviewSurface {
        BusStopArrivalItem(
            data = Data()
        )
    }
}

@Preview
@Composable
fun BusStopArrivalItemPreviewDark() {
    PreviewSurface(darkTheme = true) {
        BusStopArrivalItem(
            data = Data()
        )
    }
}