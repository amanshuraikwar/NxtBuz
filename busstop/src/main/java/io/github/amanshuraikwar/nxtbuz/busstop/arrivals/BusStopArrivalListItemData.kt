package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

sealed class BusStopArrivalListItemData {
    data class Header(val title: String) : BusStopArrivalListItemData()
    data class BusStopArrival(
        val busServiceNumber: String,
        val destinationBusStopDescription: String,
        val busLoad: BusLoad,
        val wheelchairAccess: Boolean,
        val busType: BusType,
        val arrival: String,
    ) : BusStopArrivalListItemData() {
        companion object {
            operator fun invoke() = BusStopArrival(
                "961M",
                destinationBusStopDescription = "MARINE CTR RD",
                busLoad = BusLoad.values().random(),
                wheelchairAccess = listOf(true, false).random(),
                busType = BusType.values().random(),
                arrival = "in 04 mins"
            )
        }
    }
}