package io.github.amanshuraikwar.howmuch.data.model

data class ArrivingBus(
    val origin: ArrivingBusStop,
    val destination: ArrivingBusStop,
    val arrival: String,
    val latitude: Double,
    val longitude: Double,
    val visitNumber: Int,
    val load: BusLoad,
    val feature: String
    //val type: BusType
)

data class ArrivingBusStop(
    val busStopCode: String,
    val roadName: String,
    val busStopDescription: String
)

enum class BusLoad {
    SEA, SDA, LSD
}

enum class BusType {
    SD, DD, BD
}