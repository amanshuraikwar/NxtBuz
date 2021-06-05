package io.github.amanshuraikwar.nxtbuz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteScreen
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsScreen
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzApp
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.common.util.startSettingsActivity
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMap
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchScreen
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState
import io.github.amanshuraikwar.nxtbuz.search.ui.model.rememberSearchState
import io.github.amanshuraikwar.nxtbuz.starred.StarredBusArrivals
import io.github.amanshuraikwar.nxtbuz.ui.model.MainScreenState
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionUtil: PermissionUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    private lateinit var vm: MainViewModel

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = viewModelProvider(viewModelFactory)
        makeStatusBarTransparent()
        setContent {
            NxtBuzApp {
                Box {
                    val screenState by vm.screenState.collectAsState()

                    NxtBuzMap(
                        Modifier.fillMaxSize(),
                        viewModelProvider(viewModelFactory)
                    )

                    val searchState = rememberSearchState(
                        vm = viewModelProvider(viewModelFactory)
                    )

                    val backHandlerEnabled =
                        searchState.screenState != SearchScreenState.Nothing

                    BackHandler(backHandlerEnabled) {
                        searchState.clear()
                    }

                    if (searchState.searchBarPadding != 0.dp) {
                        StarredBusArrivals(
                            modifier = Modifier
                                .padding(
                                    top = searchState.searchBarPadding
                                ),
                            vm = viewModelProvider(viewModelFactory),
                            onItemClicked = { busStopCode, busServiceNumber ->
                                vm.onBusServiceClick(
                                    busStopCode = busStopCode,
                                    busServiceNumber = busServiceNumber
                                )
                            }
                        )
                    }

                    SearchScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        searchState = searchState,
                        onBusStopSelected = { busStop ->
                            vm.onBusStopClick(busStop)
                        },
                        onSettingsClicked = {
                            startSettingsActivity()
                        }
                    )

                    val offsetY = if (searchState.screenState is SearchScreenState.Nothing) {
                        0.dp
                    } else {
                        LocalConfiguration.current.screenHeightDp.dp
                    }

                    Box(
                        Modifier.offset(y = offsetY)
                    ) {
                        ContentNavGraph(
                            screenState = screenState,
                            onBusStopClick = { busStop ->
                                vm.onBusStopClick(busStop)
                            },
                            onBusServiceClick = { busStopCode, busServiceNumber ->
                                vm.onBusServiceClick(busStopCode, busServiceNumber)
                            }
                        )
                    }
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    fun ContentNavGraph(
        screenState: MainScreenState,
        onBusStopClick: (BusStop) -> Unit,
        onBusServiceClick: (busStopCode: String, busServiceNumber: String) -> Unit,
    ) {
        when (screenState) {
            is MainScreenState.BusRoute -> {
                BusRouteScreen(
                    vm = viewModelProvider(viewModelFactory),
                    busStopCode = screenState.busStopCode,
                    busServiceNumber = screenState.busServiceNumber
                )
            }
            is MainScreenState.BusStopArrivals -> {
                BusStopArrivalsScreen(
                    vm = viewModelProvider(viewModelFactory),
                    busStop = screenState.busStop,
                    onBusServiceClick = onBusServiceClick
                )
            }
            MainScreenState.BusStops -> {
                BusStopsScreen(
                    vm = viewModelProvider(viewModelFactory),
                    onBusStopClick = onBusStopClick,
                )
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
        if (requestCode > LocationUtil.REQUEST_CHECK_SETTINGS) {
            locationUtil.onResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (!vm.onBackPressed()) {
            finish()
        }
    }
}