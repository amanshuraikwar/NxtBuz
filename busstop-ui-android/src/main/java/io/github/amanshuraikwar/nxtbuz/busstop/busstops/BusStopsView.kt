package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GpsOff
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.WrongLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsScreenState
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.StopsFilter

@ExperimentalMaterialApi
@Composable
fun BusStopsView(
    state: BusStopsScreenState,
    padding: PaddingValues,
    onBusStopClick: (busStopCode: String) -> Unit,
    onBusStopStarToggle: (busStopCode: String, newStarState: Boolean) -> Unit,
    onRetry: () -> Unit = {},
    onUseDefaultLocation: () -> Unit = {},
    onStopsFilterClick: (StopsFilter) -> Unit = {}
) {
    when (state) {
        BusStopsScreenState.Failed -> {
            // TODO-amanshuraikwar (30 Jan 2022 12:28:42 PM): implement
        }
        BusStopsScreenState.Fetching -> {
            FetchingView(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = padding),
                "Fetching..."
            )
        }
        is BusStopsScreenState.NearbyBusStops -> {
            Column(
                Modifier.fillMaxWidth()
            ) {
                StopsFilterView(
                    filter = state.filter,
                    onStopsFilterClick = onStopsFilterClick
                )

                when (state) {
                    is BusStopsScreenState.NearbyBusStops.Fetching -> {
                        FetchingView(
                            Modifier
                                .fillMaxWidth()
                                .padding(paddingValues = padding),
                            "Fetching nearby bus stops..."
                        )
                    }
                    is BusStopsScreenState.NearbyBusStops.LocationError -> {
                        LocationErrorView(
                            title = state.title,
                            primaryButtonText = state.primaryButtonText,
                            onPrimaryButtonClick = state.onPrimaryButtonClick,
                            secondaryButtonText = state.secondaryButtonText,
                            onSecondaryButtonClick = state.onSecondaryButtonClick
                        )
                    }
                    is BusStopsScreenState.NearbyBusStops.Success -> {
                        if (state.listItems.isEmpty()) {
                            NoBusStopsErrorView(
                                icon = Icons.Rounded.WrongLocation,
                                title = "There seem to be no bus stops near you :(",
                                primaryButtonText = "RETRY",
                                onPrimaryButtonClick = onRetry,
                                secondaryButtonText = "USE DEFAULT LOCATION",
                                onSecondaryButtonClick = onUseDefaultLocation
                            )
                        } else {
                            NearbyBusStopsView(
                                state.listItems,
                                padding,
                                onBusStopClick,
                                onBusStopStarToggle
                            )
                        }
                    }
                }
            }
        }
        BusStopsScreenState.DefaultLocationBusStops.Fetching -> {
            FetchingView(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = padding),
                "Fetching bus stops..."
            )
        }
        BusStopsScreenState.StarredBusStops.Fetching -> {
            FetchingView(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = padding),
                "Fetching starred bus stops..."
            )
        }
        is BusStopsScreenState.DefaultLocationBusStops.Success -> {
            if (state.listItems.isEmpty()) {
                NoBusStopsErrorView(
                    icon = Icons.Rounded.GpsOff,
                    title = "There seem to be no bus stops near your default location :(",
                    primaryButtonText = "RETRY",
                    onPrimaryButtonClick = onRetry,
                    secondaryButtonText = "USE DEFAULT LOCATION",
                    onSecondaryButtonClick = onUseDefaultLocation
                )
            } else {
                NearbyBusStopsView(
                    state.listItems,
                    padding,
                    onBusStopClick,
                    onBusStopStarToggle,
                )
            }
        }
        is BusStopsScreenState.StarredBusStops.Success -> {
            if (state.listItems.isEmpty()) {
                NoBusStopsErrorView(
                    icon = Icons.Rounded.StarOutline,
                    title = "You have not starred any bus stops yet :(",
                    primaryButtonText = "SEE NEARBY BUS STOPS",
                    onPrimaryButtonClick = onRetry,
                    secondaryButtonText = "USE DEFAULT LOCATION",
                    onSecondaryButtonClick = onUseDefaultLocation
                )
            } else {
                NearbyBusStopsView(
                    state.listItems,
                    padding,
                    onBusStopClick,
                    onBusStopStarToggle,
                )
            }
        }
    }
}