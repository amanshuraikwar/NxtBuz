package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.busstop.BusStopActivity
import io.github.amanshuraikwar.howmuch.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.howmuch.ui.search.SearchActivity
import io.github.amanshuraikwar.howmuch.util.PermissionUtil
import io.github.amanshuraikwar.howmuch.util.lerp
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import kotlinx.android.synthetic.main.bus_stops_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_overview.*
import javax.inject.Inject


private const val TAG = "OverviewFragment"

class OverviewFragment : DaggerFragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var permissionUtil: PermissionUtil

    private lateinit var viewModel: OverviewViewModel

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(contentContainer) { _, insets ->
            //searchBg.setMarginTop(insets.systemWindowInsetTop)
            screenTopGuideline.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        setupViewModel()
        setUpBottomSheet()
//        searchIb.setOnClickListener {
//            if (searchTiet.text?.isEmpty() == true) {
//                // TODO: 5/4/20
//            } else {
//                searchTiet.setText("")
//            }
//        }
//        searchTiet.addTextChangedListener after@{
//            if (it?.length == 1) {
//                if (searchIb.tag == "1") {
//                    return@after
//                }
//                val animated =
//                    AnimatedVectorDrawableCompat.create(
//                        activity!!,
//                        R.drawable.avd_anim_settings_to_clear_24
//                    )
//                searchIb.setImageDrawable(animated)
//                animated?.start()
//                searchIb.tag = "1"
//            } else if (it?.length == 0) {
//                val animated =
//                    AnimatedVectorDrawableCompat.create(
//                        activity!!,
//                        R.drawable.avd_anim_clear_to_settings_24
//                    )
//                searchIb.setImageDrawable(animated)
//                animated?.start()
//                searchIb.tag = "0"
//            }
//        }
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { activity!!.windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.setBottomSheetCallback(
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

    private fun showLoading() {
        val animated =
            AnimatedVectorDrawableCompat.create(activity!!, R.drawable.avd_anim_get_nearby)
        loadingIv.setImageDrawable(animated)
        animated?.start()
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
                EventObserver {
                    val fragment = SupportMapFragment.newInstance(
                        GoogleMapOptions()
                            .camera(
                                CameraPosition.Builder().target(LatLng(it.first, it.second))
                                    .zoom(14f)
                                    .build()
                            )
                    )
                    fragment.getMapAsync(this)
                    childFragmentManager.beginTransaction().replace(R.id.mapFragment, fragment)
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

            viewModel.busStops.observe(
                this,
                Observer {
                    val adapter =
                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), it)
                    itemsRv.layoutManager = LinearLayoutManager(activity)
                    itemsRv.adapter = adapter
                }
            )

            viewModel.busStopActivity.observe(
                this,
                EventObserver { busStop ->
                    startBusStopActivity(busStop)
                }
            )

            viewModel.goto.observe(
                this,
                EventObserver { busStop ->
                    gotoBusStop(busStop)
                }
            )

            viewModel.loading.observe(
                this,
                EventObserver {
                    if (it) {
                        showLoading()
                    } else {
                        hideLoading()
                    }
                }
            )

            viewModel.mapCenter.observe(
                this,
                EventObserver {
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(it.first, it.second)
                        )
                    )
                }
            )

            viewModel.mapMarker.observe(
                this,
                EventObserver {
                    if (it.second) {
                        googleMap?.clear()
                    }
                    it.first.forEach {
                        googleMap?.addMarker(it)
                    }
                }
            )

            viewModel.locationStatus.observe(
                this,
                EventObserver {
                    recenterFab.setImageResource(
                        if (it) {
                            R.drawable.ic_round_my_location_24
                        } else {
                            R.drawable.ic_round_gps_off_24
                        }
                    )
                }
            )

            viewModel.clearMap.observe(
                this,
                EventObserver {
                    googleMap?.clear()
                }
            )

            viewModel.mapCircle.observe(
                this,
                EventObserver {
                    googleMap?.clear()
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    it.first.first,
                                    it.first.second
                                )
                            )
                            .icon(
                                bitmapDescriptorFromVector(
                                    activity,
                                    R.drawable.ic_location_marker_24_32
                                )
                            )
                            .title("center")
                    )
                    googleMap?.addCircle(
                        CircleOptions()
                            .center(LatLng(it.first.first, it.first.second))
                            .radius(it.second)
                            .fillColor(ContextCompat.getColor(activity, R.color.orange_transparent))
                            .strokeWidth(4f)
                            .strokeColor(ContextCompat.getColor(activity, R.color.orange))
                    )
                }
            )

            recenterFab.setOnClickListener {
                viewModel.onRecenterClicked()
            }

            searchMtv.setOnClickListener {
                startActivity(Intent(activity, SearchActivity::class.java))
            }

//            searchTiet.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    viewModel.searchBusStops(searchTiet.text.toString())
//                    searchTiet.clearFocus()
//                    val imm =
//                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(searchTiet.windowToken, 0)
//                    return@OnEditorActionListener true
//                }
//                false
//            })
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        );
        val canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private fun startBusStopActivity(busStop: BusStop) {
        startActivity(Intent(activity, BusStopActivity::class.java).putExtra("busStop", busStop))
    }

    private fun gotoBusStop(busStop: BusStop) {
        val browserIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "https://www.google.com/maps/dir/" +
                            "?api=1" +
                            "&destination=${busStop.latitude},${busStop.longitude}&travelmode=walking"
                )
            )
        startActivity(browserIntent)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        viewModel.mapReady = googleMap != null
        this.googleMap = googleMap
        this.googleMap?.setMinZoomPreference(14f)
        this.googleMap?.animateCamera(
            CameraUpdateFactory.scrollBy(
                0f,
                Point().let { activity!!.windowManager.defaultDisplay.getSize(it); it.y } / 3f
            )
        )
        this.googleMap?.setOnMarkerClickListener {
            it.showInfoWindow()
            true
        }
        this.googleMap?.setOnMapLongClickListener {
            viewModel.fetchBusStopsForLatLon(it.latitude, it.longitude)
        }
    }
}