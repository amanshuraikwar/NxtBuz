package io.github.amanshuraikwar.nxtbuz.commonkmm

data class TrainStop(
    val type: String,
    val code: String,
    val hasFacilities: Boolean,
    val hasDepartureTimes: Boolean,
    val hasTravelAssistance: Boolean,
    val name: String,
    val lat: Double,
    val lng: Double,
    val starred: Boolean,
)
