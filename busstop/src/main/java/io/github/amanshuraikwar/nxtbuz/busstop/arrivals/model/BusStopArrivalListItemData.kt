package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusLoad
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusType

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
            val busLoad: BusLoad,
            val wheelchairAccess: Boolean,
            val busType: BusType,
            val arrival: Int,
        ) : BusStopArrival(busServiceNumber, busStop, starred) {
            fun copy(
                arrival: Int,
                busStop: BusStop,
                destinationBusStopDescription: String,
                busType: BusType,
                wheelchairAccess: Boolean,
                busLoad: BusLoad,
                starred: Boolean,
            ): Arriving {
                return Arriving(
                    busServiceNumber = busServiceNumber,
                    destinationBusStopDescription = destinationBusStopDescription,
                    busLoad = busLoad,
                    wheelchairAccess = wheelchairAccess,
                    busType = busType,
                    arrival = arrival,
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
                    busLoad = busLoad,
                    wheelchairAccess = wheelchairAccess,
                    busType = busType,
                    arrival = arrival,
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
    ) : BusStopArrivalListItemData()
}