package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import io.github.amanshuraikwar.nxtbuz.busroute.ui.model.BusRouteListItemData
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals

@ExperimentalAnimationApi
@Composable
fun BusArrivalItem(
    arrivalState: BusRouteListItemData.ArrivalState,
    position: BusRouteListItemData.BusRouteNode.Position,
    contentColor: Color,
) {
    AnimatedVisibility(
        visible = arrivalState !is BusRouteListItemData.ArrivalState.Inactive,
        enter = fadeIn() +
                expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(300)
                ),
        exit = fadeOut() +
                shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(600)
                ),
    ) {
        BarAvatarItem(
            drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
            barColor = contentColor,
        ) {
            Box {
                AnimatedVisibility(
                    visible = arrivalState is BusRouteListItemData.ArrivalState.Fetching,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    NotArrivingItem(
                        message = "Fetching arrivals...",
                    )
                }

                AnimatedVisibility(
                    visible = arrivalState is BusRouteListItemData.ArrivalState.Active,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    (arrivalState as? BusRouteListItemData.ArrivalState.Active)?.let { state ->
                        when (val arrivals = state.busArrivals) {
                            is BusArrivals.Arriving -> {
                                ArrivingItem(
                                    busArrivals = arrivals,
                                    lastUpdatedOn = state.lastUpdatedOn
                                )
                            }
                            BusArrivals.DataNotAvailable -> {
                                NotArrivingItem(
                                    message = "Data not Available",
                                    lastUpdatedOn = state.lastUpdatedOn
                                )
                            }
                            BusArrivals.NotOperating -> {
                                NotArrivingItem(
                                    message = "Not Operating",
                                    lastUpdatedOn = state.lastUpdatedOn
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

