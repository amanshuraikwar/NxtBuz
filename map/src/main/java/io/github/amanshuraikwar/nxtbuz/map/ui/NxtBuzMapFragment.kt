package io.github.amanshuraikwar.nxtbuz.map.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.common.model.EventObserver
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.map.R
import javax.inject.Inject

/**
 * Handles the [GoogleMap] component of the NxtBuz app.
 * @author amanshuraikwar
 * @since 24 Jan 2021 03:29:57 PM
 */
class NxtBuzMapFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: NxtBuzMapViewModel

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_nxt_buz_map_fragment, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        if (savedInstanceState != null) {
            viewModel.onReCreate()
        }
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.initMap.observe(
            viewLifecycleOwner,
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
    }
}