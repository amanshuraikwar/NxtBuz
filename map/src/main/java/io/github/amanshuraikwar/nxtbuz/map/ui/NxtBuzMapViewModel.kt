package io.github.amanshuraikwar.nxtbuz.map.ui

import android.os.Handler
import android.os.Looper
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
import io.github.amanshuraikwar.nxtbuz.common.util.map.MapUtil
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultMapZoomUseCase
import io.github.amanshuraikwar.nxtbuz.map.LocationViewModelDelegate
import io.github.amanshuraikwar.nxtbuz.map.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named


private const val TAG = "NxtBuzMapViewModel"

/**
 * [ViewModel] for [NxtBuzMapFragment].
 * @author amanshuraikwar
 * @since 24 Jan 2021 03:29:23 PM
 */
class NxtBuzMapViewModel @Inject constructor(
    private val defaultLocationUseCase: DefaultLocationUseCase,
    private val defaultMapZoomUseCase: DefaultMapZoomUseCase,
    @Named("mapEventFlow") private val mapEventFlow: MutableSharedFlow<MapEvent>,
    @Named("mapCenter") private val mapCenter: MutableStateFlow<LatLng?>,
    private val mapViewModelDelegate: LocationViewModelDelegate,
    private val mapUtil: MapUtil,
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

    private val markerSet = mutableSetOf<MapMarker>()
    private val markerMap = mutableMapOf<String, Marker>()
    private val routeSet = mutableSetOf<MapEvent.AddRoute>()
    private val routeMap = mutableMapOf<String, Polyline>()

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
    }

    private val recreateLock = Mutex()

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

                    if (map == googleMap) return@MapInitData

                    map = googleMap
                    map?.setOnCameraIdleListener {
                        viewModelScope.launch {
                            mapCenter.value = map?.cameraPosition?.target ?: return@launch
                        }
                    }

                    if (markerSet.isNotEmpty()) {
                        viewModelScope.launch {
                            recreateLock.withLock {
                                for (marker in markerSet) {
                                    map?.addMarker(marker)
                                }
                            }
                        }
                    }

                    if (routeSet.isNotEmpty()) {
                        viewModelScope.launch {
                            recreateLock.withLock {
                                for (route in routeSet) {
                                    map?.addRoute(route)
                                }
                            }
                        }
                    }
                }
            )

            launch {
                while (map == null) {
                    delay(300)
                }
            }
        }

        startCollectingEvents(viewModelScope)

        mapViewModelDelegate.init(viewModelScope)
    }

    private fun startCollectingEvents(coroutineScope: CoroutineScope) {
        coroutineScope.launch(dispatcherProvider.computation) {
            mapEventFlow.collect { mapEvent ->
                when (mapEvent) {
                    is MapEvent.ClearMap -> {
                        map?.clearMap()
                    }
                    is MapEvent.MoveCenter -> {
                        map?.moveCenter(mapEvent)
                    }
                    is MapEvent.DeleteMarkers -> {
                        deleteMarkers(mapEvent)
                    }
                    is MapEvent.AddCircle -> {
                        map?.addCircle(mapEvent)
                    }
                    is MapEvent.AddRoute -> {
                        map?.addRoute(mapEvent)
                    }
                    is MapEvent.DeleteRoute -> {
                        deleteRoute(mapEvent)
                    }
                    is MapEvent.AddMarker -> {
                        map?.addMarker(mapEvent.marker)
                    }
                    is MapEvent.DeleteMarker -> {
                        deleteMarker(mapEvent.markerId)
                    }
                    is MapEvent.AddMarkers -> {
                        map?.addMarkers(mapEvent.markerList)
                    }
                    is MapEvent.MoveMarker -> {
                        moveMarker(mapEvent.markerId, mapEvent.newPosition)
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

    private val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private suspend inline fun <T> runOnMainLooper(
        crossinline predicate: () -> T
    ): T {
        return suspendCancellableCoroutine { cont ->
            mainHandler.post {
                val result = predicate()
                if (cont.isActive) {
                    cont.resumeWith(Result.success(result))
                }
            }
        }
    }

    private suspend fun GoogleMap.clearMap() {
        runOnMainLooper {
            clear()
        }
    }

    private suspend fun GoogleMap.addMarkers(
        mapMarkerList: List<MapMarker>
    ) {
        val markerOptionsList = withContext(dispatcherProvider.io) {
            mapMarkerList.map { mapMarker ->
                MarkerOptions()
                    .position(
                        LatLng(
                            mapMarker.lat,
                            mapMarker.lng
                        )
                    )
                    .icon(
                        mapUtil.bitmapDescriptorFromVector(mapMarker.iconDrawableRes)
                    )
                    .title(mapMarker.description)
                    .flat(mapMarker.isFlat)
            }
        }

        runOnMainLooper {
            markerOptionsList.mapIndexed { index, markerOptions ->
                val marker = addMarker(markerOptions)
                markerMap[mapMarkerList[index].id] = marker
            }
        }

        mapMarkerList.forEach { mapMarker ->
            markerSet.add(mapMarker)
        }
    }

    private suspend fun deleteMarker(
        markerId: String,
    ) {
        withContext(dispatcherProvider.map) {
            val marker = markerMap[markerId]
            if (marker != null) {
                runOnMainLooper {
                    marker.remove()
                }
                markerMap.remove(markerId)
                markerSet.removeAll { mapMarker ->
                    mapMarker.id == markerId
                }
            }
        }
    }

    private suspend fun GoogleMap.addMarker(
        mapMarker: MapMarker,
    ) {
        withContext(dispatcherProvider.map) {
            val markerOption =
                MarkerOptions()
                    .position(
                        LatLng(
                            mapMarker.lat,
                            mapMarker.lng
                        )
                    )
                    .icon(
                        mapUtil.bitmapDescriptorFromVector(mapMarker.iconDrawableRes)
                    )
                    .title(mapMarker.description)
                    .flat(mapMarker.isFlat)

            val marker = runOnMainLooper {
                addMarker(markerOption)
            }

            markerMap[mapMarker.id] = marker
            markerSet.add(mapMarker)
        }
    }

    private suspend fun moveMarker(
        markerId: String,
        newPosition: LatLng,
    ) {
        runOnMainLooper {
            markerMap[markerId]?.position = newPosition
        }
    }

    private suspend fun GoogleMap.moveCenter(mapEvent: MapEvent.MoveCenter) {
        runOnMainLooper {
            animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(mapEvent.lat, mapEvent.lng)
                ),
                1000,
                object : GoogleMap.CancelableCallback {
                    override fun onFinish() {
                    }

                    override fun onCancel() {
                    }
                }
            )
        }
    }

    private suspend fun deleteMarkers(mapEvent: MapEvent.DeleteMarkers) {
        runOnMainLooper {
            mapEvent.markerList.forEach { marker ->
                marker.remove()
            }
        }
    }

    private suspend fun GoogleMap.addCircle(
        addCircle: MapEvent.AddCircle
    ) {
        return runOnMainLooper {
            addCircle(
                mapUtil.getLocationCircleOptions(
                    addCircle.lat,
                    addCircle.lng,
                    addCircle.radius
                )
            )
        }
    }

    private suspend fun GoogleMap.addRoute(
        mapEvent: MapEvent.AddRoute
    ) {
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

        val markerList = mutableListOf<Marker>()

        val polyline: Polyline = runOnMainLooper {
            val polyline: Polyline = addPolyline(opts)
            mapEvent.latLngList.forEach { (lat, lng) ->
                markerList.add(
                    addMarker(
                        MarkerOptions().icon(bitmapDescriptor).position(LatLng(lat, lng))
                            .flat(true)
                            .anchor(0.5f, 0.5f)
                    )
                )
            }
            polyline
        }

        routeSet.add(mapEvent)

        routeMap[mapEvent.routeId] = polyline

        markerList.forEach { marker ->
            markerMap["${mapEvent.routeId}-${marker.id}"] = marker
        }
    }

    private suspend fun deleteRoute(mapEvent: MapEvent.DeleteRoute) {
        val polyline = routeMap[mapEvent.routeId]
        if (polyline != null) {
            runOnMainLooper {
                polyline.remove()
            }
            routeMap.remove(mapEvent.routeId)
            routeSet.removeAll { addRoute ->
                addRoute.routeId == mapEvent.routeId
            }
            runOnMainLooper {
                markerMap.keys
                    .filter { it.startsWith(mapEvent.routeId) }
                    .forEach {
                        markerMap.remove(it)?.remove()
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        map = null
    }
}