package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import io.github.amanshuraikwar.howmuch.util.MY_PERMISSIONS_REQUEST_FINE_LOCATION
import io.github.amanshuraikwar.howmuch.util.PermissionUtil
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        setUpBottomSheet()
        searchTiet.addTextChangedListener after@{
            if (it?.length == 1) {
                if (searchIb.tag == "1") {
                    return@after
                }
                val animated =
                    AnimatedVectorDrawableCompat.create(
                        activity!!,
                        R.drawable.avd_anim_settings_to_clear_24
                    )
                searchIb.setImageDrawable(animated)
                animated?.start()
                searchIb.tag = "1"
            } else if (it?.length == 0) {
                val animated =
                    AnimatedVectorDrawableCompat.create(
                        activity!!,
                        R.drawable.avd_anim_clear_to_settings_24
                    )
                searchIb.setImageDrawable(animated)
                animated?.start()
                searchIb.tag = "0"
            }
        }
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

                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        searchBg.visibility = View.VISIBLE
                        recenterFab.hide()
                    } else {
                        searchBg.visibility = View.INVISIBLE
                        recenterFab.show()
                    }
                }

            }
        )
    }

    private fun showLoading() {
        val animated =
            AnimatedVectorDrawableCompat.create(activity!!, R.drawable.avd_anim_get_nearby)
//        animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
//            override fun onAnimationEnd(drawable: Drawable?) {
//                Log.d(TAG, "onAnimationEnd: Animation end.")
//                runCatching { loadingIv.postDelayed({ animated.start() }, 1000) }
//            }
//
//        })
        loadingIv.setImageDrawable(animated)
        animated?.start()
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory) {
                colorControlNormalResId = TypedValue().let {
                    ContextCompat.getColor(activity, R.color.color_distribution_bar_def)
                }
            }

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
                    onGotoClicked(busStop)
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

            viewModel.busStopMarker.observe(
                this,
                EventObserver {
                    it.forEach {
                        googleMap?.addMarker(
                            MarkerOptions()
                                .position(
                                    LatLng(
                                        it.latitude,
                                        it.longitude
                                    )
                                )
                                .icon(
                                    bitmapDescriptorFromVector(
                                        activity,
                                        R.drawable.ic_marker_bus_stop_48
                                    )
                                )
                                .title(it.description)
                        )
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
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        var vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight()
        );
        var bitmap = Bitmap.createBitmap(
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        );
        var canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private fun hideLoading() {
        loadingIv.visibility = View.INVISIBLE
        loadingTv.visibility = View.INVISIBLE
    }

    private fun startBusStopActivity(busStop: BusStop) {
        startActivity(Intent(activity, BusStopActivity::class.java).putExtra("busStop", busStop))
    }

    private fun onGotoClicked(busStop: BusStop) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
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