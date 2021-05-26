package io.github.amanshuraikwar.nxtbuz.common.model.arrival

import io.github.amanshuraikwar.nxtbuz.common.model.ArrivingBusStop
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

/**
 * @property arrival Arrival time in next N minutes.
 * @author amanshuraikwar
 * @since 26 May 2021 02:33:33 PM
 */
data class ArrivingBus(
    val origin: ArrivingBusStop,
    val destination: ArrivingBusStop,
    val arrival: Int,
    val latitude: Double,
    val longitude: Double,
    val visitNumber: Int,
    val load: BusLoad,
    val wheelchairAccess: Boolean,
    val type: BusType
)