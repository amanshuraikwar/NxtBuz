package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.arrival.ArrivingBus

sealed class BusStopArrivalListItemData {
    data class Header(
        val id: String,
        val title: String
    ) : BusStopArrivalListItemData()

    sealed class BusStopArrival(
        val busServiceNumber: String,
        val busStop: BusStop,
        val starred: Boolean,
        val id: String = "${busStop.code}-$busServiceNumber-arrival",
    ) : BusStopArrivalListItemData() {

        class Arriving(
            busServiceNumber: String,
            busStop: BusStop,
            starred: Boolean,
            val destinationBusStopDescription: String,
            val arrivingBusList: List<ArrivingBus>
        ) : BusStopArrival(busServiceNumber, busStop, starred) {
            fun copy(
                busStop: BusStop,
                destinationBusStopDescription: String,
                arrivingBusList: List<ArrivingBus>,
                starred: Boolean,
            ): Arriving {
                return Arriving(
                    busServiceNumber = busServiceNumber,
                    destinationBusStopDescription = destinationBusStopDescription,
                    arrivingBusList = arrivingBusList,
                    busStop = busStop,
                    starred = starred
                )
            }

            fun copy(
                starred: Boolean,
            ): Arriving {
                return Arriving(
                    busServiceNumber = busServiceNumber,
                    destinationBusStopDescription = destinationBusStopDescription,
                    arrivingBusList = arrivingBusList,
                    busStop = busStop,
                    starred = starred
                )
            }
        }

        class NotArriving(
            busServiceNumber: String,
            busStop: BusStop,
            starred: Boolean,
            val reason: String,
        ) : BusStopArrival(
            busServiceNumber,
            busStop,
            starred
        ) {
            fun copy(
                reason: String,
                starred: Boolean,
            ): NotArriving {
                return NotArriving(
                    busServiceNumber = busServiceNumber,
                    reason = reason,
                    busStop = busStop,
                    starred = starred,
                )
            }

            fun copy(starred: Boolean): NotArriving {
                return NotArriving(
                    busServiceNumber = busServiceNumber,
                    reason = reason,
                    busStop = busStop,
                    starred = starred,
                )
            }
        }
    }

    data class BusStopHeader(
        val id: String,
        val busStopDescription: String,
        val busStopRoadName: String,
        val busStopCode: String,
        private val _starred: MutableState<Boolean>,
    ) : BusStopArrivalListItemData() {
        val starred: Boolean by _starred

        fun updateStarred(newValue: Boolean) {
            _starred.value = newValue
        }

        constructor(
            id: String,
            busStopDescription: String,
            busStopRoadName: String,
            busStopCode: String,
            starred: Boolean,
        ) : this(
            id = id,
            busStopDescription = busStopDescription,
            busStopRoadName = busStopRoadName,
            busStopCode = busStopCode,
            _starred = mutableStateOf(starred),
        )
    }
}