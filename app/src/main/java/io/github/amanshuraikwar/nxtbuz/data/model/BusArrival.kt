package io.github.amanshuraikwar.nxtbuz.data.model

data class BusArrival(
    val serviceNumber: String,
    val operator: String,
    val originStopDescription: String,
    val destinationStopDescription: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    val arrivals: Arrivals
)

sealed class Arrivals(var starred: Boolean) {
    class NotOperating(starred: Boolean) : Arrivals(starred)
    class Arriving(
        starred: Boolean,
        val arrivingBusList: List<ArrivingBus>
    ) : Arrivals(starred)
}