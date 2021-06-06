package io.github.amanshuraikwar.nxtbuz.map.ui

import android.os.Handler
import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.suspendCoroutine
import android.os.Looper
import java.lang.Runnable


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
    @Named("markerClicked") private val markerClickedFlow: MutableStateFlow<Marker?>,
    @Named("mapCenter") private val mapCenter: MutableStateFlow<LatLng?>,
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

    private fun onMarkerClicked(marker: Marker) {
        viewModelScope.launch(dispatcherProvider.computation) {
            markerClickedFlow.value = marker
        }
    }

    private fun startCollectingEvents(coroutineScope: CoroutineScope) {
        coroutineScope.launch(dispatcherProvider.computation) {
            mapEventFlow.collect { mapEvent ->
                when (mapEvent) {
                    is MapEvent.ClearMap -> {
                        clearMap()
                        MapResult.EmptyResult
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

                        map?.addRoute(mapEvent)


                        MapResult.EmptyResult
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
                        MapResult.EmptyResult
                    }
                    else -> {
                        MapResult.ErrorResult("Unsupported map event.")
                    }
                }

                MapResult.EmptyResult
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

    val markerSet = mutableSetOf<MapMarker>()
    val markerMap = mutableMapOf<String, Marker>()

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

        withContext(dispatcherProvider.main) {
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
                runOnMainLooper {//withContext(dispatcherProvider.main) {
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
                        if (mapMarker is ArrivingBusMapMarker) {
                            markerUtil.arrivingBusBitmapDescriptor(mapMarker.busServiceNumber)
                        } else {
                            mapUtil.bitmapDescriptorFromVector(mapMarker.iconDrawableRes)
                        }
                    )
                    .title(mapMarker.description)
                    .flat(mapMarker.isFlat)

            val marker = runOnMainLooper { //withContext(dispatcherProvider.main) {
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
        withContext(dispatcherProvider.map) {
            withContext(dispatcherProvider.main) {
                markerMap.get(markerId)?.position = newPosition
            }
        }
    }

    private suspend fun moveCenter(mapEvent: MapEvent.MoveCenter) {
        //withContext(dispatcherProvider.main) {
        map?.let { map ->
            //suspendCoroutine<Unit> { cont ->
            runOnMainLooper {
                map.animateCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(mapEvent.lat, mapEvent.lng)
                    ),
                    1000,
                    object : GoogleMap.CancelableCallback {
                        override fun onFinish() {
                            //cont.resumeWith(Result.success(Unit))
                        }

                        override fun onCancel() {
                            //cont.resumeWith(Result.success(Unit))
                        }
                    }
                )
            }
        }
        //}
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
        //return withContext(dispatcherProvider.main) {
        return runOnMainLooper {
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

    private val routeSet = mutableSetOf<MapEvent.AddRoute>()
    private val routeMap = mutableMapOf<String, Polyline>()

    private val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
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
//            withContext(dispatcherProvider.main) {
//                polyline.remove()
//            }
            routeMap.remove(mapEvent.routeId)
            routeSet.removeAll { addRoute ->
                addRoute.routeId == mapEvent.routeId
            }
            //withContext(dispatcherProvider.main) {
            //suspendCancellableCoroutine<Unit> { cont ->
            //Handler(Looper.getMainLooper()).post {
            runOnMainLooper {
                markerMap.keys
                    .filter { it.startsWith(mapEvent.routeId) }
                    .forEach {
                        markerMap.remove(it)?.remove()
                    }
            }
            //cont.resumeWith(Result.success(Unit))
            //}

            //}
        }
    }

    override fun onCleared() {
        super.onCleared()
        map = null
    }
}