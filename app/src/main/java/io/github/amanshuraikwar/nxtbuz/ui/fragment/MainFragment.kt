package io.github.amanshuraikwar.nxtbuz.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.busroute.ui.BusRouteScreen
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsScreen
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.listitem.*
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMap
import io.github.amanshuraikwar.nxtbuz.onboarding.permission.PermissionDialog
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchBar
import io.github.amanshuraikwar.nxtbuz.search.ui.SearchScreen
import io.github.amanshuraikwar.nxtbuz.starred.ui.StarredBusArrivalsActivity
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.StarredBusArrivalOptionsDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@InternalCoroutinesApi
class MainFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionDialog: PermissionDialog

    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var starredBusArrivalsAdapter: MultiItemAdapter<RecyclerViewTypeFactoryGenerated>? =
        null
    private var adapter: MultiItemAdapter<RecyclerViewTypeFactoryGenerated>? = null
    private lateinit var screenStateFragment: FragmentContainerView

    @ExperimentalComposeUiApi
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NxtBuzTheme {
                    ProvideWindowInsets {
                        Box {
                            NxtBuzMap(Modifier.fillMaxSize(), viewModelProvider(viewModelFactory))

                            val navController = rememberNavController()

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

                            SearchScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                vm = viewModelProvider(viewModelFactory),
                                onBusStopSelected = { busStop ->
                                    // see: https://wajahatkarim.com/2021/03/pass-parcelable-compose-navigation/
                                    navController.currentBackStackEntry?.arguments?.putParcelable(
                                        "busStop",
                                        busStop
                                    )
                                    navController.navigate("busStopArrival")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun goToBusStopArrivals(busStop: BusStop) {
    }

    private fun goToBusRoute(busServiceNumber: String, busStop: BusStop?) {
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.bottomSheetSlideOffset.observe(
                viewLifecycleOwner,
                { slideOffset ->
//                    searchBg.alpha = lerp(
//                        0f, 1f, 0f, 1f, slideOffset
//                    )
//                    recenterFab.alpha = lerp(
//                        1f, 0f, 0f, 1f, slideOffset
//                    )
                }
            )

            lifecycleScope.launch {
                viewModel.navigateToBusStopArrivals.collect { busStop ->
                    goToBusStopArrivals(busStop)
                }
            }

            lifecycleScope.launch {
                // TODO-amanshuraikwar (17 Feb 2021 12:06:37 AM): do not de-construct here
                viewModel.navigateToBusRoute.collect { (busServiceNumber, busStop) ->
                    goToBusRoute(busServiceNumber, busStop)
                }
            }

            viewModel.locationStatus.observe(viewLifecycleOwner) {
//                recenterFab.setImageResource(
//                    if (it) {
//                        R.drawable.ic_near_me_24
//                    } else {
//                        R.drawable.ic_near_me_disabled_24
//                    }
//                )
//                recenterFab.tag = if (it) {
//                    "no_error"
//                } else {
//                    "error"
//                }
            }

            viewModel.starredListItems.observe(
                viewLifecycleOwner,
                Observer { listItems ->
//                    val layoutState = starredBusArrivalsRv.layoutManager?.onSaveInstanceState()
//                    starredBusArrivalsAdapter =
//                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
//                    starredBusArrivalsRv.layoutManager?.onRestoreInstanceState(layoutState)
//                    starredBusArrivalsRv.adapter = starredBusArrivalsAdapter ?: return@Observer
                }
            )

            viewModel.startStarredBusArrivalActivity.observe(
                viewLifecycleOwner,
                EventObserver {
                    startActivityForResult(
                        Intent(activity, StarredBusArrivalsActivity::class.java),
                        REQUEST_STARRED_BUS_ARRIVALS
                    )
                }
            )

            viewModel.starredBusArrivalRemoved.observe(
                viewLifecycleOwner,
                EventObserver { (busStop, busServiceNumber) ->
                    starredBusArrivalsAdapter?.remove { item ->
                        if (item is StarredBusArrivalItem) {
                            return@remove item.starredBusArrival.busStopCode == busStop.code
                                    && item.starredBusArrival.busServiceNumber == busServiceNumber
                        }
                        if (item is StarredBusArrivalErrorItem) {
                            return@remove item.starredBusArrival.busStopCode == busStop.code
                                    && item.starredBusArrival.busServiceNumber == busServiceNumber
                        }
                        return@remove false
                    }
                    if (starredBusArrivalsAdapter?.items?.size == 1) {
                        starredBusArrivalsAdapter?.remove { true }
                    }
                }
            )

            viewModel.starredBusArrivalOptionsDialog.observe(
                viewLifecycleOwner,
                EventObserver { (busStop, busServiceNumber) ->
                    StarredBusArrivalOptionsDialogFragment(
                        busStop,
                        busServiceNumber
                    ).show(
                        childFragmentManager, "starred-bus-arrival-options"
                    )
                }
            )

            viewModel.starToggleState.observe(
                viewLifecycleOwner,
                EventObserver { (busStopCode, busServiceNumber, newToggleState) ->
                    adapter
                        ?.items
                        ?.indexOfFirst { item ->

//                            if (item is BusArrivalCompactItem) {
//                                if (
//                                    item.busStopCode == busStopCode
//                                    && item.busArrival.serviceNumber == busServiceNumber
//                                    && item.busArrival.starred != newToggleState
//                                ) {
//                                    item.busArrival.starred = newToggleState
//                                    return@indexOfFirst true
//                                }
//                            }

//                            if (item is BusArrivalErrorItem) {
//                                if (
//                                    item.busStopCode == busStopCode
//                                    && item.busArrival.serviceNumber == busServiceNumber
//                                    && item.busArrival.starred != newToggleState
//                                ) {
//                                    item.busArrival.starred = newToggleState
//                                    return@indexOfFirst true
//                                }
//                            }

                            if (item is BusRouteHeaderItem) {
                                if (
                                    item.busStopCode == busStopCode
                                    && item.busServiceNumber == busServiceNumber
                                    && item.starred != newToggleState
                                ) {
                                    item.starred = newToggleState
                                    return@indexOfFirst true
                                }
                            }

                            false
                        }
                        ?.let { index ->
                            adapter?.notifyItemChanged(index)
                        }
                }
            )

            permissionDialog.init(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SEARCH_BUS_STOPS) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<BusStop>("bus_stop")?.let { busStop ->
                    viewModel.onBusStopClicked(busStop)
                    return
                }
                data?.getParcelableExtra<BusService>("bus_service")?.let { busService ->
                    viewModel.onBusServiceClicked(busService.busServiceNumber)
                    return
                }
            }
        }
        if (requestCode == REQUEST_STARRED_BUS_ARRIVALS) {
            if (resultCode == Activity.RESULT_OK) {
                val starredBusArrivalClicked =
                    data?.getParcelableExtra<StarredBusArrivalClicked>(
                        StarredBusArrivalsActivity.KEY_STARRED_BUS_ARRIVAL_CLICKED
                    ) ?: return
                viewModel.onBusServiceClicked(
                    starredBusArrivalClicked.busStop, starredBusArrivalClicked.busServiceNumber
                )
            }
        }
    }

    companion object {
        private const val REQUEST_SEARCH_BUS_STOPS = 10001
        private const val REQUEST_STARRED_BUS_ARRIVALS = 10002
    }
}