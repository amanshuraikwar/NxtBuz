package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals

@Composable
fun BusArrivalItem(
    arrivalState: BusRouteListItemData.ArrivalState,
    position: BusRouteListItemData.BusRouteNode.Position,
    contentColor: Color,
) {
    Crossfade(targetState = arrivalState) { state ->
        when(state) {
            is BusRouteListItemData.ArrivalState.Inactive -> { }
            else -> {
                BarAvatarItem(
                    drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
                    barColor = contentColor,
                ) {
                    if (state is BusRouteListItemData.ArrivalState.Active) {
                        when (val arrivals = state.arrivals) {
                            is Arrivals.Arriving -> {
                                ArrivingItem(
                                    arrivals = arrivals,
                                    lastUpdatedOn = state.lastUpdatedOn
                                )
                            }
                            Arrivals.DataNotAvailable -> {
                                NotArrivingItem(
                                    message = "Data not Available",
                                    lastUpdatedOn = state.lastUpdatedOn
                                )
                            }
                            Arrivals.NotOperating -> {
                                NotArrivingItem(
                                    message = "Not Operating",
                                    lastUpdatedOn = state.lastUpdatedOn
                                )
                            }
                        }
                    }
                    if (state is BusRouteListItemData.ArrivalState.Fetching) {
                        NotArrivingItem(
                            message = "Fetching arrivals...",
                        )
                    }
                }
            }
        }
    }
}

