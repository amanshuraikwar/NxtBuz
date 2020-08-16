package io.github.amanshuraikwar.nxtbuz.common.model

data class ArrivingBus(
    val origin: ArrivingBusStop,
    val destination: ArrivingBusStop,
    val arrival: String,
    val latitude: Double,
    val longitude: Double,
    val visitNumber: Int,
    val load: BusLoad,
    val feature: String,
    val type: BusType
)





