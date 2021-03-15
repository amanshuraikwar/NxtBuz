package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

sealed class BusStopArrivalListItemData {
    data class Header(val title: String) : BusStopArrivalListItemData()

    sealed class BusStopArrival(
        val busServiceNumber: String,
    ) : BusStopArrivalListItemData() {

        class Arriving(
            busServiceNumber: String,
            val destinationBusStopDescription: String,
            val busLoad: BusLoad,
            val wheelchairAccess: Boolean,
            val busType: BusType,
            val arrival: String,
        ) : BusStopArrival(busServiceNumber) {
            fun copy(
                arrival: String,
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
                   arrival = arrival
               )
            }
        }

        class NotArriving(
            busServiceNumber: String,
            val reason: String,
        ) : BusStopArrival(
            busServiceNumber
        ) {
            fun copy(reason: String): NotArriving {
                return NotArriving(
                    busServiceNumber = busServiceNumber,
                    reason = reason
                )
            }
        }

        companion object {
            operator fun invoke() = BusStopArrival.Arriving(
                "961M",
                destinationBusStopDescription = "MARINE CTR RD",
                busLoad = BusLoad.values().random(),
                wheelchairAccess = listOf(true, false).random(),
                busType = BusType.values().random(),
                arrival = "in 04 mins"
            )
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