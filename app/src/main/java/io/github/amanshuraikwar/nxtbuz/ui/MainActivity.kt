package io.github.amanshuraikwar.nxtbuz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsViewModel
import io.github.amanshuraikwar.nxtbuz.busstop.busstops.BusStopsViewModel
import io.github.amanshuraikwar.nxtbuz.common.compose.NxtBuzApp
import io.github.amanshuraikwar.nxtbuz.common.util.NavigationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.makeStatusBarTransparent
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.common.util.startSettingsActivity
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMapViewModel
import io.github.amanshuraikwar.nxtbuz.map.ui.recenter.RecenterViewModel
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchViewModel
import io.github.amanshuraikwar.nxtbuz.starred.StarredViewModel
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

    private val mainViewModel: MainViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val busRouteViewModel: BusRouteViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val busStopsViewModel: BusStopsViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val busStopArrivalsViewModel: BusStopArrivalsViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val nxtBuzMapViewModel: NxtBuzMapViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val starredViewModel: StarredViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val searchViewModel: SearchViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    private val recenterViewModel: RecenterViewModel by lazy {
        viewModelProvider(viewModelFactory)
    }

    @ExperimentalAnimatedInsets
    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        setContent {
            NxtBuzApp {
                val screenState by mainViewModel.screenState.collectAsState()
                MainScreen(
                    screenState = screenState,
                    mainViewModel = mainViewModel,
                    busStopsViewModel = busStopsViewModel,
                    busStopArrivalsViewModel = busStopArrivalsViewModel,
                    busRouteViewModel = busRouteViewModel,
                    nxtBuzMapViewModel = nxtBuzMapViewModel,
                    searchViewModel = searchViewModel,
                    starredViewModel = starredViewModel,
                    recenterViewModel = recenterViewModel,
                    onSettingsClick = ::startSettingsActivity
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.onInit()
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
        if (!mainViewModel.onBackPressed()) {
            finish()
        }
    }
}