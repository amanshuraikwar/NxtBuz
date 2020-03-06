package io.github.amanshuraikwar.howmuch.data.model

data class BusArrival(
    val serviceNumber: String,
    val operator: String,
    val destinationStopDescription: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val arrivals: Arrivals
)

sealed class Arrivals {
    object NotOperating : Arrivals()
    data class Arriving(val arrivingBusList: List<ArrivingBus>) : Arrivals()
}