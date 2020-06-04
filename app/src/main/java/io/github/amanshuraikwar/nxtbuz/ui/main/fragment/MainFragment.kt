package io.github.amanshuraikwar.nxtbuz.ui.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.busstop.model.BusStop
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.nxtbuz.ui.permission.PermissionDialog
import io.github.amanshuraikwar.nxtbuz.ui.search.SearchActivity
import io.github.amanshuraikwar.nxtbuz.ui.settings.SettingsActivity
import io.github.amanshuraikwar.nxtbuz.ui.starred.StarredBusArrivalsActivity
import io.github.amanshuraikwar.nxtbuz.ui.starred.model.StarredBusArrivalClicked
import io.github.amanshuraikwar.nxtbuz.util.lerp
import io.github.amanshuraikwar.nxtbuz.util.setMarginTop
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import kotlinx.android.synthetic.main.bus_stops_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { _, insets ->
            screenTopGuideline.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        itemsRv.layoutManager = LinearLayoutManager(activity)
        starredBusArrivalsRv.layoutManager = LinearLayoutManager(
            activity, RecyclerView.HORIZONTAL, false
        )

        setupViewModel()
        setupBottomSheet()

        if (savedInstanceState != null) {
            viewModel.onReCreate()
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { activity!!.windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    searchBg.alpha =
                        lerp(
                            0f, 1f, 0f, 1f, slideOffset
                        )
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
                }

            }
        )
    }

    private fun hideLoading() {
        loadingIv.visibility = View.INVISIBLE
        loadingTv.visibility = View.INVISIBLE
        itemsRv.visibility = View.VISIBLE
    }

    private fun showLoading(loading: Loading.Show) {
        val animated =
            AnimatedVectorDrawableCompat.create(
                activity!!, loading.avd
            )
        loadingIv.setImageDrawable(animated)
        animated?.start()
        loadingTv.text = loading.txt
        itemsRv.visibility = View.GONE
        loadingIv.visibility = View.VISIBLE
        loadingTv.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory)

            viewModel.initMap.observe(
                this,
                EventObserver { mapInitData ->
                    val fragment = SupportMapFragment.newInstance(
                        GoogleMapOptions()
                            .camera(
                                CameraPosition.Builder().target(mapInitData.latLng)
                                    .zoom(mapInitData.zoom)
                                    .build()
                            )
                            .compassEnabled(false)
                            .mapToolbarEnabled(false)
                            .minZoomPreference(mapInitData.zoom)
                    )
                    fragment.getMapAsync { googleMap: GoogleMap? ->
                        mapInitData.onMapReady(googleMap)
                    }
                    fragment.retainInstance = true
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.mapFragment, fragment)
                        .commit()
                }
            )

            viewModel.error.observe(
                this,
                EventObserver {
                    itemsRv.visibility = View.GONE
                    loadingIv.visibility = View.VISIBLE
                    loadingTv.visibility = View.VISIBLE
                    loadingIv.setImageResource(it.iconResId)
                    loadingTv.text = it.msg
                }
            )

            viewModel.listItems.observe(
                this,
                Observer { listItems ->
                    val layoutState = itemsRv.layoutManager?.onSaveInstanceState()
                    val adapter =
                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
                    itemsRv.layoutManager?.onRestoreInstanceState(layoutState)
                    itemsRv.adapter = adapter
                }
            )

            viewModel.collapseBottomSheet.observe(
                this,
                EventObserver {
                    itemsRv.smoothScrollToPosition(0)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            )

            viewModel.loading.observe(
                this,
                Observer { loading ->
                    if (loading is Loading.Show) {
                        showLoading(loading)
                    } else {
                        hideLoading()
                    }
                }
            )

            viewModel.locationStatus.observe(
                this,
                Observer {
                    recenterFab.setImageResource(
                        if (it) {
                            R.drawable.ic_round_my_location_24
                        } else {
                            R.drawable.ic_round_gps_off_24
                        }
                    )
                    recenterFab.tag = if (it) {
                        "no_error"
                    } else {
                        "error"
                    }
                }
            )

            viewModel.showBack.observe(
                this,
                Observer { show ->
                    backFab.visibility = if (show) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            )

            viewModel.onBackPressed.observe(
                this,
                EventObserver {
                    when {
                        bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            itemsRv.scrollToPosition(0)
                        }
                        backFab.isVisible -> {
                            viewModel.onBackPressed()
                        }
                        else -> {
                            activity.finish()
                        }
                    }
                }
            )

            viewModel.starredListItems.observe(
                this,
                Observer { listItems ->
                    val layoutState = starredBusArrivalsRv.layoutManager?.onSaveInstanceState()
                    val adapter =
                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
                    starredBusArrivalsRv.layoutManager?.onRestoreInstanceState(layoutState)
                    starredBusArrivalsRv.adapter = adapter
                }
            )

            viewModel.startStarredBusArrivalActivity.observe(
                this,
                EventObserver {
                    startActivityForResult(
                        Intent(activity, StarredBusArrivalsActivity::class.java),
                        REQUEST_STARRED_BUS_ARRIVALS
                    )
                }
            )

            permissionDialog.init(this)

            recenterFab.setOnClickListener {
                if (recenterFab.tag == "error") {
                    permissionDialog.show {
                        viewModel.onRecenterClicked()
                    }
                } else {
                    viewModel.onRecenterClicked()
                }
            }

            searchMtv.setOnClickListener {
                startActivityForResult(
                    Intent(activity, SearchActivity::class.java),
                    REQUEST_SEARCH_BUS_STOPS
                )
            }

            backFab.setOnClickListener {
                activity.onBackPressed()
            }

            settingsFab.setOnClickListener {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SEARCH_BUS_STOPS) {
            if (resultCode == Activity.RESULT_OK) {
                val busStop = data?.getParcelableExtra<BusStop>("bus_stop") ?: return
                viewModel.onBusStopClicked(busStop)
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