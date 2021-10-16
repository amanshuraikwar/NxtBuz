package io.github.amanshuraikwar.nxtbuz.commonkmm.arrival

import kotlinx.datetime.Instant

/**
 * @property arrival Arrival time in next N minutes.
 * @author amanshuraikwar
 * @since 26 May 2021 02:33:33 PM
 */
data class ArrivingBus(
    val origin: ArrivingBusStop,
    val destination: ArrivingBusStop,
    val arrival: Int,
    val arrivalInstant: Instant,
    val latitude: Double,
    val longitude: Double,
    val visitNumber: Int,
    val load: BusLoad,
    val wheelchairAccess: Boolean,
    val type: BusType
)