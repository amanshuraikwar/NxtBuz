package io.github.amanshuraikwar.nxtbuz.commonkmm

data class BusStop(
    val code: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val operatingBusList: List<Bus>
)

