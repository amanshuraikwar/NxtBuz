package io.github.amanshuraikwar.nxtbuz.remotedatasource

data class BusArrivalItemDto(
    val serviceNumber: String,
    val operator: String,
    val arrivingBus: ArrivingBusItemDto?,
    val arrivingBus1: ArrivingBusItemDto?,
    val arrivingBus2: ArrivingBusItemDto?
)