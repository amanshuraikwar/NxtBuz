package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.util.Property
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.model.AnimateUpdate
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.model.MapInitData
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.map.util.LatLngInterpolator
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.AnimateMarker
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapEvent
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.MapMarker
import io.github.amanshuraikwar.nxtbuz.util.MapUtil
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine


class MapViewModelDelegateImpl @Inject constructor(
    private val mapUtil: MapUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : MapViewModelDelegate {

    private var map: GoogleMap? = null

    private val _initMap = MutableLiveData<MapInitData>()
    override val initMap = _initMap.asEvent()

    private val mapMarkerIdMarkerMap: MutableMap<String, Marker> = mutableMapOf()
    private val mapMarkerIdMapMarkerMap: MutableMap<String, MapMarker> = mutableMapOf()
    private val mutex = Mutex()

    override suspend fun initMap(
        lat: Double,
        lng: Double,
        onMapLongClick: (lat: Double, lng: Double) -> Unit
    ) {
        if (map != null) return
        else return suspendCoroutine { cont ->

            _initMap.postValue(
                MapInitData(
                    LatLng(lat, lng),
                    // todo get from use case
                    14f
                ) { googleMap: GoogleMap? ->
                    map = googleMap
                    map?.let { mapUtil.updateMapStyle(it) }
                    cont.resumeWith(Result.success(Unit))
                    map?.setOnMapLongClickListener {
                        onMapLongClick(it.latitude, it.longitude)
                    }
                }
            )
        }
    }

    override suspend fun pushMapEvent(mapEvent: MapEvent) {
        mutex.withLock {
            when (mapEvent) {
                is MapEvent.MoveCenter -> {
                    withContext(dispatcherProvider.main) { moveCenter(mapEvent) }
                }
                is MapEvent.AddMapMarkers -> {
                    addMarkers(mapEvent)
                }
                is MapEvent.MapCircle -> {
                    addCircle(mapEvent)
                }
                is MapEvent.ClearMap -> {
                    clearMap()
                }
                is MapEvent.UpdateMapMarkers -> {
                    updateMarkers(mapEvent)
                }
                is MapEvent.DeleteMarker -> {
                    deleteMarkers(mapEvent)
                }
                is MapEvent.AddRoute -> {
                    addRoute(mapEvent)
                }
            }
        }
    }

    @UiThread
    override fun onReCreate() {
        mapUtil.updateMapStyle(map ?: return)
    }

    private suspend fun addRoute(mapEvent: MapEvent.AddRoute) =
        withContext(dispatcherProvider.computation) {

            val opts = PolylineOptions()
                .color(mapEvent.lineColor)
                .width(mapEvent.lineWidth)
                .jointType(JointType.ROUND)

            mapEvent.latLngList.forEach { opts.add(LatLng(it.first, it.second)) }

            val bitmapDescriptor =
                mapUtil.bitmapDescriptorFromVector(
                    R.drawable.ic_marker_bus_route_node_14
                )

            withContext(dispatcherProvider.main) {
                map?.addPolyline(opts)
                mapEvent.latLngList.forEach { (lat, lng) ->
                    map?.addMarker(
                        MarkerOptions().icon(bitmapDescriptor).position(LatLng(lat, lng)).flat(true)
                            .anchor(0.5f, 0.5f)
                    )
                }
            }
        }

    private suspend fun deleteMarkers(mapEvent: MapEvent.DeleteMarker) =
        withContext(dispatcherProvider.main) {
            mapEvent.mapMarkerIdList.forEach { id ->
                mapMarkerIdMarkerMap[id]?.remove()
                mapMarkerIdMarkerMap.remove(id)
            }
        }

    private suspend fun updateMarkers(mapEvent: MapEvent.UpdateMapMarkers) =
        withContext(dispatcherProvider.computation) {

            val mapMarkerIdBitmapDescriptorList = mutableListOf<Pair<String, BitmapDescriptor>>()
            val mapMarkerIdTitleList = mutableListOf<Pair<String, String>>()
            val mapMarkerIdPositionList = mutableListOf<Pair<String, LatLng>>()
            val mapMarkerIdPositionAnimatedList = mutableListOf<Pair<String, AnimateUpdate>>()

            mapEvent.mapUpdateList
                .filter { mapMarkerIdMarkerMap.containsKey(it.id) }
                .forEach { mapUpdate ->

                    if (mapUpdate.newIconDrawableRes != null) {
                        mapMarkerIdBitmapDescriptorList.add(
                            mapUpdate.id to
                                    mapUtil.bitmapDescriptorFromVector(mapUpdate.newIconDrawableRes)
                        )
                    }

                    if (mapUpdate.newDescription != null) {
                        mapMarkerIdTitleList.add(mapUpdate.id to mapUpdate.newDescription)
                    }

                    if (mapUpdate.newLat != null || mapUpdate.newLng != null) {

                        val newLat =
                            mapUpdate.newLat
                                ?: mapMarkerIdMapMarkerMap[mapUpdate.id]?.lat
                                ?: throw Exception(
                                    "No map marker found for id " +
                                            "${mapUpdate.id} but marker was found."
                                )

                        val newLng =
                            mapUpdate.newLng
                                ?: mapMarkerIdMapMarkerMap[mapUpdate.id]?.lng
                                ?: throw Exception(
                                    "No map marker found for id " +
                                            "${mapUpdate.id} but marker was found."
                                )

                        if (mapUpdate.animatePosition is AnimateMarker.Animate) {
                            mapMarkerIdPositionAnimatedList.add(
                                mapUpdate.id to AnimateUpdate(
                                    LatLng(newLat, newLng), mapUpdate.animatePosition.duration
                                )
                            )
                        } else {
                            mapMarkerIdPositionList.add(mapUpdate.id to LatLng(newLat, newLng))
                        }
                    }
                }

            withContext(dispatcherProvider.main) {

                mapMarkerIdBitmapDescriptorList.forEach { (id, newBmpDescriptor) ->
                    mapMarkerIdMarkerMap[id]?.setIcon(newBmpDescriptor)
                        ?: throw Exception("No marker found for id $id afterwards.")
                }

                mapMarkerIdTitleList.forEach { (id, newTitle) ->
                    mapMarkerIdMarkerMap[id]?.setTitle(newTitle)
                        ?: throw Exception("No marker found for id $id afterwards.")
                }

                mapMarkerIdPositionList.forEach { (id, newLatLng) ->
                    mapMarkerIdMarkerMap[id]?.setPosition(newLatLng)
                        ?: throw Exception("No marker found for id $id afterwards.")
                }

                val animatorList = mapMarkerIdPositionAnimatedList.map { (id, animateUpdate) ->
                    animateMarkerToICS(
                        mapMarkerIdMarkerMap[id]
                            ?: throw Exception("No marker found for id $id afterwards."),
                        animateUpdate.latLng,
                        animateUpdate.duration
                    )
                }

                if (animatorList.isNotEmpty()) {
                    val decSet = AnimatorSet()
                    decSet.playSequentially(animatorList)
                    decSet.start()
                }
            }
        }

    private fun animateMarkerToICS(
        marker: Marker,
        finalPosition: LatLng,
        duration: Long
    ): Animator {
        val typeEvaluator: TypeEvaluator<LatLng> =
            TypeEvaluator { fraction, startValue, endValue ->
                LatLngInterpolator.Linear().interpolate(
                    fraction,
                    startValue,
                    endValue
                )
            }
        val property: Property<Marker, LatLng> =
            Property.of(Marker::class.java, LatLng::class.java, "position")
        val animator =
            ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition)
        animator.duration = duration
        return animator
    }

    private suspend fun clearMap() =
        withContext(dispatcherProvider.main) {
            map?.clear()
            mapMarkerIdMarkerMap.clear()
            mapMarkerIdMapMarkerMap.clear()
        }

    private suspend fun addCircle(mapEvent: MapEvent.MapCircle) =
        withContext(dispatcherProvider.main) {
            map?.addCircle(mapUtil.getCircleOptions(mapEvent.lat, mapEvent.lng, mapEvent.radius))
        }

    private suspend fun addMarkers(addMapEvent: MapEvent.AddMapMarkers) =
        withContext(dispatcherProvider.io) {

            val markerList = addMapEvent.markerList.map { mapMarker ->

                val markerOptions = MarkerOptions()
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

                mapMarker to markerOptions
            }

            withContext(dispatcherProvider.main) {
                markerList.forEach { (mapMarker, markerOptions) ->
                    val marker = map?.addMarker(markerOptions)
                    mapMarkerIdMarkerMap[mapMarker.id] = marker ?: return@forEach
                    mapMarkerIdMapMarkerMap[mapMarker.id] = mapMarker
                }
            }
        }

    private suspend fun moveCenter(mapEvent: MapEvent.MoveCenter) {
        return map?.let { map ->
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
        } ?: Unit
    }

    override fun detach() {
        // todo
    }

    companion object {
        private const val TAG = "MapViewModelDelegateImp"
    }
}