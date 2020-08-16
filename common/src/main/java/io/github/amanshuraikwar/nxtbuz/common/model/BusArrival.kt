package io.github.amanshuraikwar.nxtbuz.common.model

data class BusArrival(
    val serviceNumber: String,
    val operator: String,
    val originStopDescription: String,
    val destinationStopDescription: String,
    val direction: Int,
    val stopSequence: Int,
    val distance: Double,
    var starred: Boolean,
    val arrivals: Arrivals
)

