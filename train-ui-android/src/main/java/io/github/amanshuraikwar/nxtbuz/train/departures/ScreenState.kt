package io.github.amanshuraikwar.nxtbuz.train.departures

internal sealed class ScreenState {
    object Fetching : ScreenState()
    data class Success(
        val header: TrainStopHeader,
        val listItems: List<ListItemData>
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
        val track: String,
        val trainCategoryName: String,
        val cancelled: Boolean,
        val plannedArrival: String,
        val actualArrival: String,
        val plannedDeparture: String,
        val actualDeparture: String,
        val delayedByMinutes: Int,
    ) : ListItemData()
}

internal data class TrainStopHeader(
    val code: String,
    val hasFacilities: Boolean,
    val hasDepartureTimes: Boolean,
    val hasTravelAssistance: Boolean,
    val name: String,
    val lat: Double,
    val lng: Double,
    val starred: Boolean
)