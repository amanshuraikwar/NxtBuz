package io.github.amanshuraikwar.nxtbuz.ui.main.overview.neww

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import io.github.amanshuraikwar.nxtbuz.ui.main.overview.Loading
import io.github.amanshuraikwar.nxtbuz.ui.permission.PermissionDialog
import io.github.amanshuraikwar.nxtbuz.ui.search.SearchActivity
import io.github.amanshuraikwar.nxtbuz.ui.settings.SettingsActivity
import io.github.amanshuraikwar.nxtbuz.util.isDarkTheme
import io.github.amanshuraikwar.nxtbuz.util.lerp
import io.github.amanshuraikwar.nxtbuz.util.setMarginTop
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
import kotlinx.android.synthetic.main.bus_stops_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_overview.*
import javax.inject.Inject

class OverviewFragmentNew : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionDialog: PermissionDialog

    private lateinit var viewModel: OverviewViewModelNew
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
            //searchBg.setMarginTop(insets.systemWindowInsetTop)
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

//            viewModel.error.observe(
//                this,
//                EventObserver {
//                    itemsRv.visibility = View.GONE
//                    loadingIv.visibility = View.VISIBLE
//                    loadingTv.visibility = View.VISIBLE
//                    loadingIv.setImageResource(it.iconResId)
//                    loadingTv.text = it.msg
//                }
//            )

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

//            viewModel.collapseBottomSheet.observe(
//                this,
//                EventObserver {
//                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//                }
//            )
//
//            viewModel.goto.observe(
//                this,
//                EventObserver { busStop ->
//                    gotoBusStop(busStop)
//                }
//            )
//
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

//
//            viewModel.locationStatus.observe(
//                this,
//                Observer {
//                    recenterFab.setImageResource(
//                        if (it) {
//                            R.drawable.ic_round_my_location_24
//                        } else {
//                            R.drawable.ic_round_gps_off_24
//                        }
//                    )
//                    recenterFab.tag = if (it) {
//                        "no_error"
//                    } else {
//                        "error"
//                    }
//                }
//            )
//
//            viewModel.clearMap.observe(
//                this,
//                EventObserver {
//                    googleMap?.clear()
//                }
//            )
//
//            viewModel.mapCircle.observe(
//                this,
//                Observer {
//                    googleMap?.clear()
//                    googleMap?.addMarker(
//                        MarkerOptions()
//                            .position(
//                                LatLng(
//                                    it.first.first,
//                                    it.first.second
//                                )
//                            )
//                            .icon(
//                                bitmapDescriptorFromVector(
//                                    activity,
//                                    R.drawable.ic_location_marker_24_32
//                                )
//                            )
//                            .title("center")
//                    )
//                    googleMap?.addCircle(
//                        CircleOptions()
//                            .center(LatLng(it.first.first, it.first.second))
//                            .radius(it.second)
//                            .fillColor(ContextCompat.getColor(activity, R.color.orange_transparent))
//                            .strokeWidth(4f)
//                            .strokeColor(ContextCompat.getColor(activity, R.color.orange))
//                    )
//                }
//            )
//
//            viewModel.showBack.observe(
//                this,
//                Observer { show ->
//                    backFab.visibility = if (show) {
//                        View.VISIBLE
//                    } else {
//                        View.GONE
//                    }
//                }
//            )
//
//            viewModel.onBackPressed.observe(
//                this,
//                EventObserver {
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
//                }
//            )
//
//            viewModel.starredListItems.observe(
//                this,
//                Observer { listItems ->
//                    val layoutState = starredBusArrivalsRv.layoutManager?.onSaveInstanceState()
//                    val adapter =
//                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
//                    starredBusArrivalsRv.layoutManager?.onRestoreInstanceState(layoutState)
//                    starredBusArrivalsRv.adapter = adapter
//                }
//            )

            permissionDialog.init(this)

            recenterFab.setOnClickListener {
//                if (recenterFab.tag == "error") {
//                    permissionDialog.show {
//                        viewModel.onRecenterClicked()
//                    }
//                } else {
//                    viewModel.onRecenterClicked()
//                }
            }

            searchMtv.setOnClickListener {
//                startActivityForResult(
//                    Intent(activity, SearchActivity::class.java),
//                    REQUEST_SEARCH_BUS_STOPS
//                )
            }

            backFab.setOnClickListener {
//                viewModel.onBackPressed()
            }

            settingsFab.setOnClickListener {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_SEARCH_BUS_STOPS) {
//            if (resultCode == Activity.RESULT_OK) {
//                val busStop = data?.getParcelableExtra<BusStop>("bus_stop") ?: return
//                viewModel.busStopSelected(busStop)
//            }
//        }
    }
}