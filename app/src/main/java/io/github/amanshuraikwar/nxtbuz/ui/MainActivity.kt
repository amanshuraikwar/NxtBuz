package io.github.amanshuraikwar.nxtbuz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.android.support.DaggerAppCompatActivity
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteScreen
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsScreen
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMap
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchScreen
import io.github.amanshuraikwar.nxtbuz.search.ui.model.SearchScreenState
import io.github.amanshuraikwar.nxtbuz.search.ui.model.rememberSearchState
import io.github.amanshuraikwar.nxtbuz.starred.StarredBusArrivals
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionUtil: PermissionUtil

    @Inject
    lateinit var locationUtil: LocationUtil

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        setContent {
            NxtBuzApp {

                Box {
                    NxtBuzMap(Modifier.fillMaxSize(), viewModelProvider(viewModelFactory))

                    val searchState = rememberSearchState(
                        vm = viewModelProvider(viewModelFactory)
                    )

                    val navController = rememberNavController()

                    val backHandlerEnabled =
                        searchState.screenState != SearchScreenState.Nothing

                    BackHandler(backHandlerEnabled) {
                        searchState.clear()
                    }

                    LaunchedEffect(key1 = backHandlerEnabled) {
                        navController.enableOnBackPressed(!backHandlerEnabled)
                    }

                    if (searchState.searchBarPadding != 0.dp) {
                        StarredBusArrivals(
                            modifier = Modifier
                                .padding(
                                    top = searchState.searchBarPadding
                                )
                        )
                    }

                    SearchScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        searchState = searchState,
                        onBusStopSelected = { busStop ->
                            // see: https://wajahatkarim.com/2021/03/pass-parcelable-compose-navigation/
                            navController.currentBackStackEntry?.arguments?.putParcelable(
                                "busStop",
                                busStop
                            )
                            navController.navigate("busStopArrival")
                        },
                    )

                    val offsetY = if (searchState.screenState is SearchScreenState.Nothing) {
                        0.dp
                    } else {
                        LocalConfiguration.current.screenHeightDp.dp
                    }

                    Box(
                        Modifier.offset(y = offsetY)
                    ) {
                        ContentNavGraph(navController = navController)
                    }
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    fun ContentNavGraph(navController: NavHostController) {
        NavHost(navController, startDestination = "busStops") {
            composable("busStops") {
                BusStopsScreen(
                    vm = viewModelProvider(viewModelFactory),
                    navController = navController
                )
            }

            composable(
                "busStopArrival",
            ) {
                val busStop =
                    navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getParcelable<BusStop>(
                            "busStop"
                        )

                if (busStop != null) {
                    BusStopArrivalsScreen(
                        navController = navController,
                        vm = viewModelProvider(viewModelFactory),
                        busStop = busStop,
                    )
                }
            }

            composable(
                "busRoute/{busServiceNumber}"
            ) { backStackEntry ->
                BusRouteScreen(
                    busServiceNumber = backStackEntry
                        .arguments
                        ?.getString("busServiceNumber")
                        ?: return@composable,
                    busStop = navController
                        .previousBackStackEntry
                        ?.arguments
                        ?.getParcelable(
                            "busStop"
                        ),
                    vm = viewModelProvider(viewModelFactory),
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
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode > LocationUtil.REQUEST_CHECK_SETTINGS) {
            locationUtil.onResult(requestCode, resultCode, data)
        }
    }
}