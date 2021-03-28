package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.map

import android.util.Log
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.common.model.map.ArrivingBusMapMarker
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapMarker
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapUpdate
//import io.github.amanshuraikwar.nxtbuz.map.MapViewModelDelegate
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusStopArrivalsMapMarkerHelper @Inject constructor(
    //private val mapViewModelDelegate: MapViewModelDelegate,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {

    private val serviceNumberMapMarkerMap = mutableMapOf<String, ArrivingBusMapMarker>()
    internal var mapStateId: Int = 0

    suspend fun showMapMarkers(busArrivals: List<BusArrival>): Unit =
        withContext(dispatcherProvider.computation) {

            // we collect the map marker changes
            // and then push them together afterwards
            val busAddList = mutableListOf<ArrivingBusMapMarker>()
            val busDeleteList = mutableListOf<String>()
            val busUpdateList = mutableListOf<MapUpdate>()

            busArrivals.forEach { busArrival ->

                when (busArrival.arrivals) {

                    is Arrivals.Arriving -> {

                        val nextArrivingBus = (busArrival.arrivals as Arrivals.Arriving).nextArrivingBus

                        serviceNumberMapMarkerMap[busArrival.serviceNumber]?.let { mapMarker ->

                            if (mapMarker shouldUpdateFor nextArrivingBus) {

                                serviceNumberMapMarkerMap[busArrival.serviceNumber] =
                                    mapMarker.copy(
                                        newLat = nextArrivingBus.latitude,
                                        newLng = nextArrivingBus.longitude,
                                        newDescription = nextArrivingBus.getMarkerDescription()
                                    )

                                busUpdateList.add(
                                    MapUpdate(
                                        mapMarker.id,
                                        nextArrivingBus.latitude,
                                        nextArrivingBus.longitude,
                                        newDescription = nextArrivingBus.getMarkerDescription()
                                    )
                                )
                            }

                        } ?: run {

                            val mapMarker =
                                ArrivingBusMapMarker(
                                    busArrival.serviceNumber,
                                    nextArrivingBus.latitude,
                                    nextArrivingBus.longitude,
                                    nextArrivingBus.getMarkerDescription(),
                                    busServiceNumber = busArrival.serviceNumber,
                                )

                            serviceNumberMapMarkerMap[busArrival.serviceNumber] = mapMarker
                            busAddList.add(mapMarker)
                        }
                    }
                    is Arrivals.DataNotAvailable,
                    is Arrivals.NotOperating,
                    -> {
                        serviceNumberMapMarkerMap[busArrival.serviceNumber]?.let { mapMarker ->
                            serviceNumberMapMarkerMap.remove(mapMarker.id)
                            busDeleteList.add(mapMarker.id)
                        }
                    }
                }
            }

            if (busAddList.isNotEmpty()) {
//                mapViewModelDelegate.pushMapEvent(
//                    mapStateId,
//                    MapEvent.AddMapMarkers(
//                        busAddList
//                    )
//                )
            }

            if (busDeleteList.isNotEmpty()) {
//                mapViewModelDelegate.pushMapEvent(
//                    mapStateId,
//                    MapEvent.DeleteMarker(
//                        busDeleteList
//                    )
//                )
            }

            if (busUpdateList.isNotEmpty()) {
//                mapViewModelDelegate.pushMapEvent(
//                    mapStateId,
//                    MapEvent.UpdateMapMarkers(
//                        busUpdateList
//                    )
//                )
            }

            Log.i(TAG, "showMapMarkers: added ${busAddList.size} marker(s).")
            Log.i(TAG, "showMapMarkers: deleted ${busDeleteList.size} marker(s).")
            Log.i(TAG, "showMapMarkers: updated ${busUpdateList.size} marker(s).")
        }

    fun clear() {
        serviceNumberMapMarkerMap.clear()
    }

    private fun ArrivingBus.getMarkerDescription(): String {
        return if (arrival == "Arr") {
            "ARRIVING NOW"
        } else {
            "$arrival MINS"
        }
    }

    private infix fun MapMarker.shouldUpdateFor(arrivingBus: ArrivingBus): Boolean {
        return arrivingBus.latitude != lat
                || arrivingBus.longitude != lng
    }

    companion object {
        private const val TAG = "BusStopArrivalsMapHlp"
    }
}