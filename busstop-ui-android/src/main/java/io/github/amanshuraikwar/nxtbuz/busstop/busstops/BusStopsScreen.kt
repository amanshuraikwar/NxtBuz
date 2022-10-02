package io.github.amanshuraikwar.nxtbuz.busstop.busstops

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsPadding
import io.github.amanshuraikwar.nxtbuz.common.compose.HeaderButton
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.model.BusStopsScreenState
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.rememberNxtBuzBottomSheetState
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.directions
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.disabled
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onDirections
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.onStar
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.outline
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.star

@ExperimentalMaterialApi
@Composable
fun BusStopsScreen(
    modifier: Modifier = Modifier,
    vm: BusStopsViewModel,
    bottomSheetBgOffset: Dp,
    showBottomSheet: Boolean,
    onBusStopClick: (busStopCode: String) -> Unit,
    onTrainStopClick: (trainsStopCode: String) -> Unit,
) {
    val bottomSheetState = rememberNxtBuzBottomSheetState(
        BottomSheetValue.Collapsed
    )

    val screenState by vm.screenState.collectAsState()

    LaunchedEffect(key1 = bottomSheetState.isInitialised) {
        if (bottomSheetState.isInitialised && screenState == BusStopsScreenState.Fetching) {
            vm.init()
        }
    }

    LaunchedEffect(key1 = showBottomSheet) {
        if (!showBottomSheet && screenState == BusStopsScreenState.Fetching) {
            vm.init()
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        if (showBottomSheet) {
            NxtBuzBottomSheet(
                modifier = modifier,
                state = bottomSheetState,
                bottomSheetBgOffset = bottomSheetBgOffset
            ) { padding ->
                BusStopsView(
                    state = screenState,
                    padding = padding,
                    onBusStopClick = onBusStopClick,
                    onBusStopStarToggle = vm::onBusStopStarToggle,
                    onRetry = {
                        if (screenState is BusStopsScreenState.NearbyBusStops) {
                            vm.fetchNearbyBusStops()
                        }

                        if (screenState is BusStopsScreenState.StarredBusStops) {
                            vm.fetchNearbyBusStops()
                        }

                        if (screenState is BusStopsScreenState.DefaultLocationBusStops) {
                            vm.fetchNearDefaultLocationBusStops()
                        }
                    },
                    onUseDefaultLocation = {
                        vm.fetchNearDefaultLocationBusStops()
                    },
                    onStopsFilterClick = vm::onStopsFilterClick,
                    onTrainStopClick = onTrainStopClick
                )
            }
        } else {
            Surface(
                modifier = modifier,
                elevation = 0.dp
            ) {
                BusStopsView(
                    state = screenState,
                    padding = PaddingValues(),
                    onBusStopClick = onBusStopClick,
                    onBusStopStarToggle = vm::onBusStopStarToggle,
                    onRetry = {
                        if (screenState is BusStopsScreenState.NearbyBusStops) {
                            vm.fetchNearbyBusStops()
                        }

                        if (screenState is BusStopsScreenState.StarredBusStops) {
                            vm.fetchNearbyBusStops()
                        }

                        if (screenState is BusStopsScreenState.DefaultLocationBusStops) {
                            vm.fetchNearDefaultLocationBusStops()
                        }
                    },
                    onUseDefaultLocation = {
                        vm.fetchNearDefaultLocationBusStops()
                    },
                    onStopsFilterClick = vm::onStopsFilterClick,
                    onTrainStopClick = onTrainStopClick
                )
            }
        }

        AnimatedVisibility(
            visible = screenState is BusStopsScreenState.NearbyBusStops
                    || screenState is BusStopsScreenState.DefaultLocationBusStops
                    || screenState is BusStopsScreenState.StarredBusStops,
            enter = slideInVertically(animationSpec = tween()) { it } + fadeIn(),
            exit = slideOutVertically(animationSpec = tween()) { it } + fadeOut()
        ) {
            Surface(
                elevation = 1.dp,
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Divider(
                        color = MaterialTheme.colors.outline
                    )

                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                    ) {
                        HeaderButton(
                            Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                            imageVector = Icons.Rounded.NearMe,
                            text = "Nearby",
                            onClick = {
                                vm.fetchNearbyBusStops()
                            },
                            selected = screenState is BusStopsScreenState.NearbyBusStops
                        )

                        CompositionLocalProvider(
                            LocalIndication provides rememberRipple(color = MaterialTheme.colors.star)
                        ) {
                            HeaderButton(
                                Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                imageVector = Icons.Rounded.Star,
                                text = "Starred",
                                onClick = {
                                    vm.fetchStarredBusStops()
                                },
                                selected = screenState is BusStopsScreenState.StarredBusStops,
                                outlineColor = MaterialTheme.colors.star.disabled,
                                onUnSelectedColor = MaterialTheme.colors.star,
                                selectedColor = MaterialTheme.colors.star,
                                onSelectedColor = MaterialTheme.colors.onStar
                            )
                        }

                        CompositionLocalProvider(
                            LocalIndication provides rememberRipple(
                                color = MaterialTheme.colors.directions
                            )
                        ) {
                            HeaderButton(
                                Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                imageVector = Icons.Rounded.GpsFixed,
                                text = "Near Default Location",
                                onClick = {
                                    vm.fetchNearDefaultLocationBusStops()
                                },
                                selected =
                                screenState is BusStopsScreenState.DefaultLocationBusStops,
                                outlineColor = MaterialTheme.colors.directions.disabled,
                                onUnSelectedColor = MaterialTheme.colors.directions,
                                selectedColor = MaterialTheme.colors.directions,
                                onSelectedColor = MaterialTheme.colors.onDirections
                            )
                        }
                    }
                }
            }
        }
    }
}