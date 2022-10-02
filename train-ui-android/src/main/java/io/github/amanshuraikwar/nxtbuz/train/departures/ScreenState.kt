package io.github.amanshuraikwar.nxtbuz.train.departures

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDepartureStatus

internal sealed class ScreenState {
    object Fetching : ScreenState()

    data class Success(
        val header: TrainStopHeader,
        val listItems: List<ListItemData>
    ) : ScreenState()

    data class Error(
        val message: String,
        val exception: Exception,
        val ableToReport: Boolean
    ) : ScreenState()
}

internal sealed class ListItemData {
    data class Header(
        val id: String,
        val title: String
    ) : ListItemData()

    data class Departure(
        val id: String,
        val destinationTrainStopName: String,
        val track: String?,
        val trainCategoryName: String,
        val departureStatus: TrainDepartureStatus,
        val plannedArrival: String?,
        val actualArrival: String?,
        val plannedDeparture: String,
        val actualDeparture: String?,
        val delayedByMinutes: Int,
        val viaStations: String?
    ) : ListItemData()
}

internal data class TrainStopHeader(
    val code: String,
    val codeToDisplay: String,
    val hasFacilities: Boolean,
    val hasDepartureTimes: Boolean,
    val hasTravelAssistance: Boolean,
    val name: String,
    val lat: Double,
    val lng: Double,
    val starred: Boolean
)