package io.github.amanshuraikwar.nxtbuz.remotedatasource

data class ArrivingBusItemDto(
    val originCode: String,
    val destinationCode: String,
    val estimatedArrival: String,
    val lat: String,
    val lng: String,
    val visitNumber: String,
    val load: String,
    val feature: String,
    val type: String
)