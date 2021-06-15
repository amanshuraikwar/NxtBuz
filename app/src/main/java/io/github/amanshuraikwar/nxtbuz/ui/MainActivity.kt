package io.github.amanshuraikwar.nxtbuz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteScreen
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsScreen
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzApp
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.common.util.startSettingsActivity
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMap
import io.github.amanshuraikwar.nxtbuz.map.ui.recenter.RecenterButton
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchBar
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchBarDecorationType
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchScreen
import io.github.amanshuraikwar.nxtbuz.search.ui.model.rememberSearchState
import io.github.amanshuraikwar.nxtbuz.starred.DecorationType
import io.github.amanshuraikwar.nxtbuz.starred.StarredBusArrivals
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import io.github.amanshuraikwar.nxtbuz.ui.model.NavigationState
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionUtil: PermissionUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    @Inject
    lateinit var navigationUtil: NavigationUtil

    private lateinit var vm: MainViewModel

    @ExperimentalAnimatedInsets
    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = viewModelProvider(viewModelFactory)
        makeStatusBarTransparent()
        setContent {
            NxtBuzApp {
                val screenState by vm.screenState.collectAsState()
                MainScreen(screenState = screenState)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vm.onInit()
    }

    @ExperimentalComposeUiApi
    @ExperimentalAnimatedInsets
    @ExperimentalMaterialApi
    @Composable
    fun MainScreen(
        screenState: MainScreenState
    ) {
        val density = LocalDensity.current
        val insets = LocalWindowInsets.current

        when (screenState) {
            MainScreenState.Fetching -> {

            }
            is MainScreenState.Success -> {
                if (screenState.showMap) {
                    Box {
                        NxtBuzMap(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = viewModelProvider(viewModelFactory),
                            onClick = { latLng ->
                                vm.onMapClick(latLng)
                            }
                        )

                        Column {
                            SearchBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        top = 16.dp,
                                        bottom = 8.dp
                                    ),
                                onClick = {
                                    vm.onSearchClick()
                                },
                                onSettingsClicked = {
                                    startSettingsActivity()
                                },
                                decorationType = SearchBarDecorationType.SHADOW
                            )

                            StarredBusArrivals(
                                modifier = Modifier.fillMaxWidth(),
                                vm = viewModelProvider(viewModelFactory),
                                onItemClicked = { busStopCode, busServiceNumber ->
                                    vm.onBusServiceClick(
                                        busStopCode = busStopCode,
                                        busServiceNumber = busServiceNumber
                                    )
                                },
                                decorationType = DecorationType.SHADOW
                            )

                            RecenterButton(
                                Modifier
                                    .align(Alignment.End)
                                    .padding(horizontal =  16.dp, vertical = 8.dp),
                                viewModelProvider(viewModelFactory)
                            )
                        }

                        ContentNavGraph(
                            navigationState = screenState.navigationState,
                            onBusStopClick = { busStop ->
                                vm.onBusStopClick(busStop)
                            },
                            showBottomSheet = true,
                            onBusServiceClick = { busStopCode, busServiceNumber ->
                                vm.onBusServiceClick(busStopCode, busServiceNumber)
                            },
                            bottomSheetBgOffset =
                            with(density) { insets.statusBars.top.toDp() }
                        )

                        if (screenState.navigationState is NavigationState.Search) {
                            SearchScreen(
                                searchState = rememberSearchState(
                                    vm = viewModelProvider(viewModelFactory)
                                ),
                                onBackClick = {
                                    vm.onBackPressed()
                                },
                                onBusStopSelected = { busStop ->
                                    vm.onBusStopClick(busStop = busStop, pushBackStack = false)
                                }
                            )
                        }
                    }
                } else {
                    Surface {
                        Box {
                            Column {
                                SearchBar(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .statusBarsPadding()
                                        .padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp,
                                            bottom = 8.dp
                                        ),
                                    onClick = {
                                        vm.onSearchClick()
                                    },
                                    onSettingsClicked = {
                                        startSettingsActivity()
                                    },
                                    decorationType = SearchBarDecorationType.OUTLINE
                                )

                                StarredBusArrivals(
                                    modifier = Modifier.fillMaxWidth(),
                                    vm = viewModelProvider(viewModelFactory),
                                    onItemClicked = { busStopCode, busServiceNumber ->
                                        vm.onBusServiceClick(
                                            busStopCode = busStopCode,
                                            busServiceNumber = busServiceNumber
                                        )
                                    },
                                    decorationType = DecorationType.OUTLINE
                                )

                                Spacer(Modifier.height(8.dp))

                                Divider()

                                ContentNavGraph(
                                    navigationState = screenState.navigationState,
                                    showBottomSheet = false,
                                    onBusStopClick = { busStop ->
                                        vm.onBusStopClick(busStop)
                                    },
                                    onBusServiceClick = { busStopCode, busServiceNumber ->
                                        vm.onBusServiceClick(busStopCode, busServiceNumber)
                                    },
                                    bottomSheetBgOffset =
                                    with(density) { insets.statusBars.top.toDp() }
                                )
                            }

                            if (screenState.navigationState is NavigationState.Search) {
                                SearchScreen(
                                    searchState = rememberSearchState(
                                        vm = viewModelProvider(viewModelFactory)
                                    ),
                                    onBackClick = {
                                        vm.onBackPressed()
                                    },
                                    onBusStopSelected = { busStop ->
                                        vm.onBusStopClick(busStop = busStop, pushBackStack = false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalAnimatedInsets
    @ExperimentalComposeUiApi
    @ExperimentalMaterialApi
    @Composable
    fun ContentNavGraph(
        navigationState: NavigationState,
        showBottomSheet: Boolean,
        onBusStopClick: (BusStop) -> Unit,
        onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
        bottomSheetBgOffset: Dp,
    ) {
        when (navigationState) {
            is NavigationState.BusRoute -> {
                BusRouteScreen(
                    vm = viewModelProvider(viewModelFactory),
                    busStopCode = navigationState.busStopCode,
                    busServiceNumber = navigationState.busServiceNumber,
                    bottomSheetBgOffset = bottomSheetBgOffset,
                    showBottomSheet = showBottomSheet,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is NavigationState.BusStopArrivals -> {
                BusStopArrivalsScreen(
                    vm = viewModelProvider(viewModelFactory),
                    busStop = navigationState.busStop,
                    onBusServiceClick = onBusServiceClick,
                    bottomSheetBgOffset = bottomSheetBgOffset,
                    showBottomSheet = showBottomSheet,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is NavigationState.BusStops -> {
                BusStopsScreen(
                    vm = viewModelProvider(viewModelFactory),
                    onBusStopClick = onBusStopClick,
                    bottomSheetBgOffset = bottomSheetBgOffset,
                    showBottomSheet = showBottomSheet,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            NavigationState.Search -> {
                // do nothing
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionUtil.onPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        permissionUtil.onCheckSettingResult(requestCode, resultCode, data)
        navigationUtil.onActivityResult(requestCode)
    }

    override fun onBackPressed() {
        if (!vm.onBackPressed()) {
            finish()
        }
    }
}