package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

sealed class BusStopArrivalListItemData {
    data class Header(val title: String) : BusStopArrivalListItemData()

    sealed class BusStopArrival(
        val busServiceNumber: String,
        val busStop: BusStop,
        val starred: Boolean,
    ) : BusStopArrivalListItemData() {

        class Arriving(
            busServiceNumber: String,
            busStop: BusStop,
            starred: Boolean,
            val destinationBusStopDescription: String,
            val busLoad: BusLoad,
            val wheelchairAccess: Boolean,
            val busType: BusType,
            val arrival: String,
        ) : BusStopArrival(busServiceNumber, busStop, starred) {
            fun copy(
                arrival: String,
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
        val busStopDescription: String,
        val busStopRoadName: String,
        val busStopCode: String,
    ) : BusStopArrivalListItemData() {
        companion object {
            operator fun invoke() = BusStopHeader(
                "Opp Blk 19",
                busStopRoadName = "Rebecca Rd",
                busStopCode = "123456",
            )
        }
    }
}