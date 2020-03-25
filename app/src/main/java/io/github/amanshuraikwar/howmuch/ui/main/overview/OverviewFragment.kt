package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
        (childFragmentManager.fragments.find { it is SupportMapFragment } as? SupportMapFragment)?.getMapAsync(
            this
        )
        setupViewModel()
        setUpBottomSheet()
    }

    private fun setUpBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { activity!!.windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.isHideable = false
    }

    private fun showLoading() {
        val animated =
            AnimatedVectorDrawableCompat.create(activity!!, R.drawable.avd_anim_get_nearby)
        animated?.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                Log.d(TAG, "onAnimationEnd: Animation end.")
                runCatching { loadingIv.postDelayed({ animated.start() }, 1000) }
            }

        })
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
                Observer {
//                    animateLatLngZoom(
//                        LatLng(it.first, it.second),
//                        14f,
//                        0,
//                        Point().let { activity!!.windowManager.defaultDisplay.getSize(it); it.y } / 3
//                    )
                    googleMap?.moveCamera(
                        CameraUpdateFactory.newLatLng(LatLng(it.first, it.second))
                    )
//                    googleMap?.animateCamera(
//                        CameraUpdateFactory.zoomTo(14f)
//                    )
                }
            )

            viewModel.mapMarker.observe(
                this,
                Observer {
                    this.googleMap?.clear()
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
                    it.second.forEach {
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
                                        R.drawable.ic_bus_stop_marker_stroke_24
                                    )
                                )
                                .title(it.description)
                        )
                    }
                }
            )
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

    var firstTime = true

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
        this.googleMap?.setOnCameraIdleListener {
            Log.d(TAG, "onMapReady: setOnCameraIdleListener")
            if (firstTime) {
                firstTime = false
                return@setOnCameraIdleListener
            }
            this.googleMap?.cameraPosition?.target?.run {
                viewModel.fetchData(latitude, longitude)
            }
        }
        this.googleMap?.setOnMarkerClickListener {
            it.showInfoWindow()
            true
        }
    }

    private fun animateLatLngZoom(
        latlng: LatLng,
        reqZoom: Float,
        offsetX: Int,
        offsetY: Int
    ) {

        this.googleMap?.let { mMap ->

            // Save current zoom
            val originalZoom: Float = mMap.getCameraPosition().zoom

            // Move temporarily camera zoom
            mMap.moveCamera(CameraUpdateFactory.zoomTo(reqZoom.toFloat()))
            val pointInScreen: Point = mMap.getProjection().toScreenLocation(latlng)
            val newPoint = Point()
            newPoint.x = pointInScreen.x + offsetX
            newPoint.y = pointInScreen.y + offsetY
            val newCenterLatLng: LatLng = mMap.getProjection().fromScreenLocation(newPoint)

            // Restore original zoom
            mMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom))

            // Animate a camera with new latlng center and required zoom.
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    newCenterLatLng,
                    reqZoom.toFloat()
                )
            )
        }
    }
}