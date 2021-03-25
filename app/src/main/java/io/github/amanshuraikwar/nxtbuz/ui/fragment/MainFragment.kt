package io.github.amanshuraikwar.nxtbuz.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsFragmentDirections
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsFragmentDirections
import io.github.amanshuraikwar.nxtbuz.common.model.*
import io.github.amanshuraikwar.nxtbuz.listitem.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRouteItems
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItems
import io.github.amanshuraikwar.nxtbuz.busstop.ui.items.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.common.compose.ComposeBottomSheet
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.onboarding.permission.PermissionDialog
import io.github.amanshuraikwar.nxtbuz.settings.ui.SettingsActivity
import io.github.amanshuraikwar.nxtbuz.starred.ui.StarredBusArrivalsActivity
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.StarredBusArrivalOptionsDialogFragment
import io.github.amanshuraikwar.nxtbuz.common.util.lerp
import io.github.amanshuraikwar.nxtbuz.common.util.setMarginTop
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.databinding.FragmentScreenStateContainerBinding
import io.github.amanshuraikwar.nxtbuz.databinding.FragmentScreenStateContainerBinding.inflate
import io.github.amanshuraikwar.nxtbuz.databinding.FragmentNxtBuzMapBinding
import io.github.amanshuraikwar.nxtbuz.databinding.FragmentNxtBuzMapBinding.inflate
import io.github.amanshuraikwar.nxtbuz.map.ui.NxtBuzMap
//import kotlinx.android.synthetic.main.bus_stops_bottom_sheet.*
//import kotlinx.android.synthetic.main.fragment_main.*
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
//                        val mapView = rememberMapViewWithLifecycle()
//                        MapViewContainer(mapView)
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
//                                    arguments = listOf(
//                                        navArgument("busStop") {
//                                            type = NavType.ParcelableType(BusStop::class.java)
//                                        }
//                                    )
                                ) {
                                    val busStop =
                                        navController
                                            .previousBackStackEntry
                                            ?.arguments
                                            ?.getParcelable<BusStop>(
                                                "busStop"
                                            )
                                    if (busStop != null) {
                                        BusStopArrivalItems(
                                            navController = navController,
                                            vm = viewModelProvider(viewModelFactory),
                                            busStop = busStop,
                                        )
                                    }
                                }

                                composable(
                                    "busRoute/{busServiceNumber}"
                                ) { backStackEntry ->
                                    BusRouteItems(
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
                                        //navController = navController
                                    )
                                }
                                //composable("friendslist") { FriendsList(...) }
                            }
//                        ComposeBottomSheet(
//                            modifier = Modifier
//                                .fillMaxSize(),
////                            bottomSheetState = bottomSheetState,
//                            backgroundColor = Color.Transparent,
//                            sheetContent = {
//                                Text(text = "helo\nhelo\nhelo")
//                            }
//                        ) {
//                        }
//                    FragmentAwareAndroidViewBinding(
//                        FragmentScreenStateContainerBinding::inflate,
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//                        this@MainFragment.screenStateFragment = screenStateFragment
//                    }
                        }
                    }
                }
//                Box {
//
//                    AndroidView(
//                        factory = { context ->
//                            MapView(context).apply {
//                                onCreate(Bundle())
//                            }
//                        },
//                        modifier = Modifier.fillMaxSize()
//                    ) { mapView ->
////                        val lifecycleObserver = rememberMapLifecycleObserver(mapView)
////                        val lifecycle = LifecycleOwnerAmbient.current.lifecycle
////                        onCommit(lifecycle) {
////                            lifecycle.addObserver(lifecycleObserver)
////                            onDispose {
////                                lifecycle.removeObserver(lifecycleObserver)
////                            }
////                        }
//                    }
//
//                FragmentAwareAndroidViewBinding<FragmentNxtBuzMapBinding>(
//                    FragmentNxtBuzMapBinding::inflate,
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    //this@MainFragment.screenStateFragment = screenStateFragment
//                }
//                }
            }
        }
        //return inflater.inflate(R.layout.fragment_main, container, false)
    }

//    @Composable
//    private fun MapViewContainer(map: MapView) {
//        //val currentLocation = viewModel.location.observeAsState()
//
//        AndroidView({ map }) { mapView -> }
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { _, insets ->
//            searchBarFragment.setMarginTop(insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
//            insets
//        }

        //itemsRv.layoutManager = LinearLayoutManager(activity)
//        starredBusArrivalsRv.layoutManager = LinearLayoutManager(
//            activity, RecyclerView.HORIZONTAL, false
//        )

        setupViewModel()
        //setupBottomSheet()

//        if (savedInstanceState != null) {
//            viewModel.onReCreate()
//        }
    }

    /*
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { requireActivity().windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                    searchBg.alpha =
//                        lerp(
//                            0f, 1f, 0f, 1f, slideOffset
//                        )
                    bottomSheetHandle.alpha =
                        lerp(
                            1f, 0f, 0f, 1f, slideOffset
                        )
                    recenterFab.alpha =
                        lerp(
                            1f, 0f, 0f, 1f, slideOffset
                        )
                    starredBusArrivalsRv.alpha =
                        lerp(
                            1f, 0f, 0f, 1f, slideOffset
                        )
                    bottomSheetBgView.update(slideOffset)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        recenterFab.visibility = View.INVISIBLE
                    } else {
                        recenterFab.visibility = View.VISIBLE
                    }
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        viewModel.bottomSheetCollapsed()
                    }
                }

            }
        )
    }
     */

    private fun goToBusStopArrivals(busStop: BusStop) {
        val action = BusStopsFragmentDirections.actionBusStopsToBusStopArrivals(busStop)
        screenStateFragment.findNavController().navigate(action)
    }

    private fun goToBusRoute(busServiceNumber: String, busStop: BusStop?) {
        val action = BusStopArrivalsFragmentDirections.actionBusStopArrivalsToBusRoute(
            busServiceNumber = busServiceNumber,
            busStop = busStop
        )
        screenStateFragment.findNavController().navigate(action)
    }

    private fun hideLoading() {
//        loadingIv.visibility = View.INVISIBLE
//        loadingTv.visibility = View.INVISIBLE
//        itemsRv.visibility = View.VISIBLE
    }

    private fun showLoading(loading: Loading.Show) {
//        val animated =
//            AnimatedVectorDrawableCompat.create(
//                requireActivity(), loading.avdResId
//            )
//        loadingIv.setImageDrawable(animated)
//        animated?.start()
//        loadingTv.setText(loading.messageResId)
//        itemsRv.visibility = View.GONE
//        loadingIv.visibility = View.VISIBLE
//        loadingTv.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

//            viewModel.initMap.observe(
//                viewLifecycleOwner,
//                EventObserver { mapInitData ->
//                    val fragment = SupportMapFragment.newInstance(
//                        GoogleMapOptions()
//                            .camera(
//                                CameraPosition.Builder().target(mapInitData.latLng)
//                                    .zoom(mapInitData.zoom)
//                                    .build()
//                            )
//                            .compassEnabled(false)
//                            .mapToolbarEnabled(false)
//                            .minZoomPreference(mapInitData.zoom)
//                    )
//                    fragment.getMapAsync { googleMap: GoogleMap? ->
//                        mapInitData.onMapReady(googleMap)
//                    }
//                    fragment.retainInstance = true
//                    childFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.mapFragment, fragment)
//                        .commit()
//                }
//            )

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

//            viewModel.error.observe(
//                viewLifecycleOwner,
//                EventObserver {
//                    itemsRv.visibility = View.GONE
//                    loadingIv.visibility = View.VISIBLE
//                    loadingTv.visibility = View.VISIBLE
//                    loadingIv.setImageResource(it.iconResId)
//                    loadingTv.text = it.msg
//                }
//            )

//            viewModel.listItems.observe(
//                viewLifecycleOwner,
//                Observer { listItems ->
//                    val layoutState = itemsRv.layoutManager?.onSaveInstanceState()
//                    adapter =
//                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
//                    itemsRv.layoutManager?.onRestoreInstanceState(layoutState)
//                    itemsRv.adapter = adapter ?: return@Observer
//                }
//            )
//
//            viewModel.collapseBottomSheet.observe(
//                viewLifecycleOwner,
//                EventObserver {
//                    itemsRv.smoothScrollToPosition(0)
//                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                }
//            )
//
//            viewModel.loading.observe(
//                viewLifecycleOwner,
//                Observer { loading ->
//                    if (loading is Loading.Show) {
//                        showLoading(loading)
//                    } else {
//                        hideLoading()
//                    }
//                }
//            )
//
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


            viewModel.showBack.observe(
                viewLifecycleOwner,
                Observer { show ->
//                    backFab.visibility = if (show) {
//                        View.VISIBLE
//                    } else {
//                        View.GONE
//                    }
                }
            )

            viewModel.onBackPressed.observe(
                viewLifecycleOwner,
                EventObserver {
                    if (!screenStateFragment.findNavController().navigateUp()) {
                        activity.finish()
                    }
                    //requireActivity().findNavController(R.id.screenStateFragment).navigateUp()
//                    screenStateFragment.findNavController().popBackStack()
//                    screenStateFragment.findNavController().navigateUp()
//                    when {
//                        bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
//                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                            itemsRv.scrollToPosition(0)
//                        }
//                        backFab.isVisible -> {
//                            viewModel.onBackPressed()
//                        }
//                        else -> {
//                            activity.finish()
//                        }
//                    }
                }
            )

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

//            viewModel.primaryBusArrivalUpdate.observe(viewLifecycleOwner) { busArrivalUpdate ->
//                adapter
//                    ?.items
//                    ?.indexOfFirst { item ->
//                        item is BusRouteCurrentItem && item.busStopCode == busArrivalUpdate.busStopCode
//                    }?.let { index ->
//                        if (index != -1) {
//                            (adapter?.items?.get(index) as? BusRouteCurrentItem)
//                                ?.updateBusArrivals(busArrivalUpdate)
//                            adapter?.notifyItemChanged(index)
//                        }
//                    }
//            }
//
//            viewModel.secondaryBusArrivalUpdate.observe(viewLifecycleOwner) { busArrivalUpdate ->
//                adapter
//                    ?.items
//                    ?.forEachIndexed { index, item ->
//                        if (item is BusRouteItem) {
//                            if (item.busStopCode == busArrivalUpdate.busStopCode) {
//                                item.updateBusArrivals(busArrivalUpdate)
//                                adapter?.notifyItemChanged(index)
//                            } else if (item !is BusRouteCurrentItem && item.arrivals.isNotEmpty()) {
//                                item.updateBusArrivals(BusArrivalUpdate.noRadar(item.busStopCode))
//                                adapter?.notifyItemChanged(index)
//                            }
//                        }
//                    }
//            }
//
//            viewModel.previousBusStopItems.observe(
//                viewLifecycleOwner,
//                EventObserver { previousItems ->
//
//                    adapter?.items
//                        ?.takeIf { items ->
//                            items.isNotEmpty()
//                        }
//                        ?.let rvUpdate@{ items ->
//
//                            val previousAllItemIndex =
//                                items.indexOfFirst { it is BusRoutePreviousAllItem }
//
//                            if (previousAllItemIndex == -1) return@rvUpdate
//
//                            items.removeAt(previousAllItemIndex)
//                            adapter?.notifyItemRemoved(previousAllItemIndex)
//                            items.addAll(previousAllItemIndex, previousItems)
//                            adapter?.notifyItemRangeInserted(
//                                previousAllItemIndex,
//                                previousItems.size
//                            )
//
//                            items
//                                .indexOfFirst { it is HeaderItem }
//                                .takeIf { it != -1 }?.let { index ->
//                                    (items[index] as HeaderItem).icon =
//                                        R.drawable.ic_round_unfold_less_16
//                                    adapter?.notifyItemChanged(index)
//                                }
//                        }
//                }
//            )
//
//            viewModel.hidePreviousBusStopItems.observe(
//                viewLifecycleOwner,
//                EventObserver { previousAllItem ->
//
//                    adapter?.items
//                        ?.takeIf { items ->
//                            items.isNotEmpty()
//                                    // if we don't already have previous all item
//                                    && items.find { it is BusRoutePreviousAllItem } == null
//                        }
//                        ?.let rvUpdate@{ items ->
//
//                            val firstPreviousBusStopItemIndex =
//                                items.indexOfFirst { it is BusRoutePreviousItem }
//
//                            if (firstPreviousBusStopItemIndex == -1) return@rvUpdate
//
//                            var currentBusStopItemIndex =
//                                items.indexOfFirst { it is BusRouteCurrentItem }
//
//                            if (currentBusStopItemIndex == -1) return@rvUpdate
//
//                            items.removeAll { it is BusRoutePreviousItem }
//
//                            adapter?.notifyItemRangeRemoved(
//                                firstPreviousBusStopItemIndex,
//                                currentBusStopItemIndex - firstPreviousBusStopItemIndex
//                            )
//
//                            currentBusStopItemIndex =
//                                items.indexOfFirst { it is BusRouteCurrentItem }
//
//                            if (currentBusStopItemIndex == -1) return@rvUpdate
//
//                            items.add(currentBusStopItemIndex, previousAllItem)
//
//                            adapter?.notifyItemInserted(currentBusStopItemIndex)
//
//                            items
//                                .indexOfFirst { it is HeaderItem }
//                                .takeIf { it != -1 }?.let { index ->
//                                    (items[index] as HeaderItem).icon = null
//                                    adapter?.notifyItemChanged(index)
//                                }
//                        }
//                }
//            )

            permissionDialog.init(this)

//            recenterFab.setOnClickListener {
//                if (recenterFab.tag == "error") {
//                    permissionDialog.show {
//                        viewModel.onRecenterClicked()
//                    }
//                } else {
//                    viewModel.onRecenterClicked()
//                }
//            }

//            searchMtv.setOnClickListener {
//                startActivityForResult(
//                    Intent(activity, io.github.amanshuraikwar.nxtbuz.search.SearchActivity::class.java),
//                    REQUEST_SEARCH_BUS_STOPS
//                )
//            }

//            backFab.setOnClickListener {
//                activity.onBackPressed()
//            }
//
//            settingsFab.setOnClickListener {
//                startActivity(Intent(activity, SettingsActivity::class.java))
//            }
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