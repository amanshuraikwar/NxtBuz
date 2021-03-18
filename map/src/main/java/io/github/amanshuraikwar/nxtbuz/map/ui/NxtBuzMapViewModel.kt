package io.github.amanshuraikwar.nxtbuz.map.ui

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.map.*
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.common.util.flow.ReturnableFlow
import io.github.amanshuraikwar.nxtbuz.common.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.common.util.map.MarkerUtil
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultMapZoomUseCase
import io.github.amanshuraikwar.nxtbuz.map.LocationViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.map.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.suspendCoroutine

private const val TAG = "NxtBuzMapViewModel"

/**
 * [ViewModel] for [NxtBuzMapFragment].
 * @author amanshuraikwar
 * @since 24 Jan 2021 03:29:23 PM
 */
class NxtBuzMapViewModel @Inject constructor(
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val defaultMapZoomUseCase: DefaultMapZoomUseCase,
    @Named("mapEventFlow") private val mapEventFlow: ReturnableFlow<MapEvent, MapResult>,
    @Named("markerClicked") private val markerClickedFlow: MutableStateFlow<Marker?>,
    private val mapViewModelDelegate: LocationViewModelDelegate,
    private val mapUtil: MapUtil,
    private val markerUtil: MarkerUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : ViewModel() {

    private var map: GoogleMap? = null

    private val _initMap = MutableLiveData<MapInitData>()
    val initMap = _initMap.asEvent()

    private val _initMapFlow = MutableSharedFlow<MapInitData?>(replay = 1)
    val initMapFlow: SharedFlow<MapInitData?> = _initMapFlow

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    init {
        init()
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
    }

    private fun init() {
        viewModelScope.launch(errorHandler) {
            val (defaultLat, defaultLng) = defaultLocationUseCase()
            val defaultMapZoom = defaultMapZoomUseCase()
            initMap(defaultLat, defaultLng, defaultMapZoom)
            Log.d(TAG, "init: initMap() complete")
        }
        mapViewModelDelegate.init(viewModelScope)
    }

    /**
     * Initialise map at given latitude and longitude.
     */
    private suspend fun initMap(
        lat: Double,
        lng: Double,
        mapZoom: Float,
    ) {
        if (map != null) return

        withContext(dispatcherProvider.computation) {
            _initMapFlow.emit(
                MapInitData(
                    LatLng(lat, lng),
                    mapZoom,
                ) { googleMap: GoogleMap? ->
                    map = googleMap
                    Log.d(TAG, "initMap: onMapReady() called")
                    //map?.let { mapUtil.updateMapStyle(it) }
                    map?.setOnMarkerClickListener {
                        onMarkerClicked(it)
                        true
                    }
                }
            )

            launch {
                while (map == null) {
                    delay(300)
                }
            }
            // suspend until the map is initialised in the UI
//            suspendCancellableCoroutine<Unit> { cont ->
//                _initMap.postValue(
//                    MapInitData(
//                        LatLng(lat, lng),
//                        mapZoom,
//                    ) { googleMap: GoogleMap? ->
//                        map = googleMap
//                        map?.let { mapUtil.updateMapStyle(it) }
//                        map?.setOnMarkerClickListener {
//                            onMarkerClicked(it)
//                            true
//                        }
//                        cont.resumeWith(Result.success(Unit))
//                    }
//                )
//            }
        }

        startCollectingEvents(viewModelScope)
    }

    private fun onMarkerClicked(marker: Marker) {
        viewModelScope.launch(dispatcherProvider.computation) {
            markerClickedFlow.value = marker
        }
    }

    private fun startCollectingEvents(coroutineScope: CoroutineScope) {
        coroutineScope.launch(dispatcherProvider.computation) {
            mapEventFlow
                .collect { mapEvent ->
                    return@collect when (mapEvent) {
                        is MapEvent.ClearMap -> {
                            clearMap()
                            MapResult.EmptyResult
                        }
                        is MapEvent.AddMapMarkers -> {
                            map?.let { addMarkers(it, mapEvent) }
                                ?: MapResult.ErrorResult("Google map is null.")
                        }
                        is MapEvent.MoveCenter -> {
                            moveCenter(mapEvent)
                            MapResult.EmptyResult
                        }
                        is MapEvent.DeleteMarkers -> {
                            deleteMarkers(mapEvent)
                            MapResult.EmptyResult
                        }
                        is MapEvent.AddCircle -> {
                            map?.let { addCircle(it, mapEvent) }
                                ?: MapResult.ErrorResult("Google map is null.")
                        }
                        is MapEvent.AddRoute -> {
                            map?.let { addRoute(it, mapEvent) }
                                ?: MapResult.ErrorResult("Google map is null.")
                        }
                        else -> {
                            MapResult.ErrorResult("Unsupported map event.")
                        }
                    }
                }
        }
    }

    @UiThread
    fun onReCreate() {
        // update map style in case of dark mode changes
        mapUtil.updateMapStyle(map ?: return)
    }

    private suspend fun clearMap() {
        withContext(dispatcherProvider.main) {
            map?.clear()
        }
    }

    private suspend fun addMarkers(
        map: GoogleMap,
        addMapEvent: MapEvent.AddMapMarkers
    ): MapResult.AddMapMarkersResult {
        val markerOptionsList = withContext(dispatcherProvider.io) {
            addMapEvent.markerList.map { mapMarker ->
                MarkerOptions()
                    .position(
                        LatLng(
                            mapMarker.lat,
                            mapMarker.lng
                        )
                    )
                    .icon(
                        // TODO: 19/1/21 refactor?
                        if (mapMarker is ArrivingBusMapMarker) {
                            markerUtil.arrivingBusBitmapDescriptor(mapMarker.busServiceNumber)
                        } else {
                            mapUtil.bitmapDescriptorFromVector(mapMarker.iconDrawableRes)
                        }
                    )
                    .title(mapMarker.description)
                    .flat(mapMarker.isFlat)
            }
        }

        val markerList = withContext(dispatcherProvider.main) {
            markerOptionsList.map { markerOptions ->
                map.addMarker(markerOptions)
            }
        }

        return MapResult.AddMapMarkersResult(markerList)
    }

    private suspend fun moveCenter(mapEvent: MapEvent.MoveCenter) {
        withContext(dispatcherProvider.main) {
            map?.let { map ->
                suspendCoroutine<Unit> { cont ->
                    map.animateCamera(
                        CameraUpdateFactory.newLatLng(
                            LatLng(mapEvent.lat, mapEvent.lng)
                        ),
                        1000,
                        object : GoogleMap.CancelableCallback {
                            override fun onFinish() {
                                cont.resumeWith(Result.success(Unit))
                            }

                            override fun onCancel() {
                                cont.resumeWith(Result.success(Unit))
                            }
                        }
                    )
                }
            }
        }
    }

    private suspend fun deleteMarkers(mapEvent: MapEvent.DeleteMarkers) {
        withContext(dispatcherProvider.main) {
            mapEvent.markerList.forEach { marker ->
                marker.remove()
            }
        }
    }

    private suspend fun addCircle(
        map: GoogleMap,
        addCircle: MapEvent.AddCircle
    ): MapResult.AddCircleResult {
        return withContext(dispatcherProvider.main) {
            MapResult.AddCircleResult(
                map.addCircle(
                    mapUtil.getLocationCircleOptions(
                        addCircle.lat,
                        addCircle.lng,
                        addCircle.radius
                    )
                )
            )
        }
    }

    private suspend fun addRoute(
        map: GoogleMap,
        mapEvent: MapEvent.AddRoute
    ): MapResult.AddRouteResult {

        val opts: PolylineOptions
        withContext(dispatcherProvider.computation) {
            opts = PolylineOptions()
                .color(mapEvent.lineColor)
                .width(mapEvent.lineWidth)
                .jointType(JointType.ROUND)
            mapEvent.latLngList.forEach { opts.add(LatLng(it.first, it.second)) }
        }


        val bitmapDescriptor = withContext(dispatcherProvider.io) {
            mapUtil.bitmapDescriptorFromVector(
                R.drawable.ic_marker_bus_route_node_14
            )
        }

        val polyline: Polyline
        val markerList = mutableListOf<Marker>()
        withContext(dispatcherProvider.main) {
            polyline = map.addPolyline(opts)
            mapEvent.latLngList.forEach { (lat, lng) ->
                markerList.add(
                    map.addMarker(
                        MarkerOptions().icon(bitmapDescriptor).position(LatLng(lat, lng)).flat(true)
                            .anchor(0.5f, 0.5f)
                    )
                )
            }
        }

        return MapResult.AddRouteResult(
            polyline = polyline,
            markerList = markerList,
        )
    }
}