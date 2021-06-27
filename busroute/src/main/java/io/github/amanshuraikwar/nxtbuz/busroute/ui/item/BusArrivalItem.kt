package io.github.amanshuraikwar.nxtbuz.busroute.ui.item

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    val alpha by animateFloatAsState(
        targetValue = if (arrivalState is BusRouteListItemData.ArrivalState.Fetching) {
            1f
        } else {
            0f
        }
    )
    if (arrivalState !is BusRouteListItemData.ArrivalState.Inactive) {
        BarAvatarItem(
            drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
            barColor = contentColor,
        ) {
            Box {
                AnimatedVisibility(
                    visible = arrivalState !is BusRouteListItemData.ArrivalState.Inactive,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    NotArrivingItem(
                        modifier = Modifier.alpha(alpha),
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
//        AnimatedVisibility(visible = arrivalState is BusRouteListItemData.ArrivalState.Fetching) {
//            BarAvatarItem(
//                drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
//                barColor = contentColor,
//            ) {
//                NotArrivingItem(
//                    message = "Fetching arrivals...",
//                )
//            }
//        }
    }
//    AnimatedVisibility(visible = arrivalState is BusRouteListItemData.ArrivalState.Fetching) {
//        BarAvatarItem(
//            drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
//            barColor = contentColor,
//        ) {
//            NotArrivingItem(
//                message = "Fetching arrivals...",
//            )
//        }
//    }

//    AnimatedVisibility(
//        visible = arrivalState is BusRouteListItemData.ArrivalState.Active
//    ) {
//        (arrivalState as? BusRouteListItemData.ArrivalState.Active)?.let { state ->
//            BarAvatarItem(
//                drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
//                barColor = contentColor,
//            ) {
//                when (val arrivals = state.busArrivals) {
//                    is BusArrivals.Arriving -> {
//                        ArrivingItem(
//                            busArrivals = arrivals,
//                            lastUpdatedOn = state.lastUpdatedOn
//                        )
//                    }
//                    BusArrivals.DataNotAvailable -> {
//                        NotArrivingItem(
//                            message = "Data not Available",
//                            lastUpdatedOn = state.lastUpdatedOn
//                        )
//                    }
//                    BusArrivals.NotOperating -> {
//                        NotArrivingItem(
//                            message = "Not Operating",
//                            lastUpdatedOn = state.lastUpdatedOn
//                        )
//                    }
//                }
//            }
//        }
//
//    }
//    arrivalState.let { state ->
//        //Crossfade(targetState = arrivalState) { state ->
//        when (state) {
//            is BusRouteListItemData.ArrivalState.Inactive -> {
//            }
//            is BusRouteListItemData.ArrivalState.Fetching -> {
//                NotArrivingItem(
//                    message = "Fetching arrivals...",
//                )
//            }
//            is BusRouteListItemData.ArrivalState.Active -> {
//                BarAvatarItem(
//                    drawBar = position != BusRouteListItemData.BusRouteNode.Position.DESTINATION,
//                    barColor = contentColor,
//                ) {
//                    when (val arrivals = state.busArrivals) {
//                        is BusArrivals.Arriving -> {
//                            ArrivingItem(
//                                busArrivals = arrivals,
//                                lastUpdatedOn = state.lastUpdatedOn
//                            )
//                        }
//                        BusArrivals.DataNotAvailable -> {
//                            NotArrivingItem(
//                                message = "Data not Available",
//                                lastUpdatedOn = state.lastUpdatedOn
//                            )
//                        }
//                        BusArrivals.NotOperating -> {
//                            NotArrivingItem(
//                                message = "Not Operating",
//                                lastUpdatedOn = state.lastUpdatedOn
//                            )
//                        }
//                    }
//                }
//            }
//        }
//        //}
//    }
}

