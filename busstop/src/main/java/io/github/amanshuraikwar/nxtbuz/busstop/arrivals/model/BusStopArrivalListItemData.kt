package io.github.amanshuraikwar.nxtbuz.busstop.arrivals.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

sealed class BusStopArrivalListItemData {
    data class Header(val title: String) : BusStopArrivalListItemData()

    sealed class BusStopArrival(
        val busServiceNumber: String,
        val busStop: BusStop,
    ) : BusStopArrivalListItemData() {

        class Arriving(
            busServiceNumber: String,
            busStop: BusStop,
            val destinationBusStopDescription: String,
            val busLoad: BusLoad,
            val wheelchairAccess: Boolean,
            val busType: BusType,
            val arrival: String,
        ) : BusStopArrival(busServiceNumber, busStop) {
            fun copy(
                arrival: String,
                busStop: BusStop,
                destinationBusStopDescription: String,
                busType: BusType,
                wheelchairAccess: Boolean,
                busLoad: BusLoad
            ): Arriving {
               return Arriving(
                   busServiceNumber = busServiceNumber,
                   destinationBusStopDescription = destinationBusStopDescription,
                   busLoad = busLoad,
                   wheelchairAccess = wheelchairAccess,
                   busType = busType,
                   arrival = arrival,
                   busStop = busStop,
               )
            }
        }

        class NotArriving(
            busServiceNumber: String,
            busStop: BusStop,
            val reason: String,
        ) : BusStopArrival(
            busServiceNumber,
            busStop
        ) {
            fun copy(reason: String): NotArriving {
                return NotArriving(
                    busServiceNumber = busServiceNumber,
                    reason = reason,
                    busStop = busStop,
                )
            }
        }

        companion object {
//            operator fun invoke() = BusStopArrival.Arriving(
//                "961M",
//                destinationBusStopDescription = "MARINE CTR RD",
//                busLoad = BusLoad.values().random(),
//                wheelchairAccess = listOf(true, false).random(),
//                busType = BusType.values().random(),
//                arrival = "in 04 mins",
////                busStop = busStop,
//            )
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