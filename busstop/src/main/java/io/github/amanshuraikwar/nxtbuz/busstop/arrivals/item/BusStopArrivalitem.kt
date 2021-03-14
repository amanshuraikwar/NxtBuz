package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.theme.star
import io.github.amanshuraikwar.nxtbuz.busstop.util.PreviewSurface
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BusStopArrivalItems(vm: ComposeTestViewModel = viewModel()) {

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        backgroundColor = Color.Transparent,
        scaffoldState = bottomSheetScaffoldState,
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
                when(busLoad){
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