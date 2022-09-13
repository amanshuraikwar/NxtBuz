package io.github.amanshuraikwar.nxtbuz.commonkmm.train

import kotlinx.datetime.Instant

data class TrainDeparture(
    val id: String,
    val destinationTrainStopName: String,
    val track: String,
    val trainCategoryName: String,
    val plannedArrivalInstant: Instant,
    val actualArrivalInstant: Instant,
    val plannedDepartureInstant: Instant,
    val actualDepartureInstant: Instant,
    val delayedByMinutes: Int,
    val departureStatus: TrainDepartureStatus,
)

enum class TrainDepartureStatus {
    INCOMING, ON_STATION, CANCELLED, UNKNOWN
}