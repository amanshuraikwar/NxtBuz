package io.github.amanshuraikwar.nxtbuz.busstop.busstops.model

sealed class BusStopsScreenState {
    object Fetching : BusStopsScreenState()

    object Failed : BusStopsScreenState()

    sealed class NearbyBusStops(
        val filter: StopsFilter
    ) : BusStopsScreenState() {
        class Fetching(filter: StopsFilter) : NearbyBusStops(filter)

        class LocationError(
            filter: StopsFilter,
            val title: String,
            val primaryButtonText: String,
            val onPrimaryButtonClick: () -> Unit,
            val secondaryButtonText: String,
            val onSecondaryButtonClick: () -> Unit
        ) : NearbyBusStops(filter)

        class Success(
            filter: StopsFilter,
            val listItems: List<BusStopsItemData>
        ) : NearbyBusStops(filter)
    }

    sealed class DefaultLocationBusStops : BusStopsScreenState() {
        object Fetching : DefaultLocationBusStops()

        data class Success(val listItems: List<BusStopsItemData>) : DefaultLocationBusStops()
    }

    sealed class StarredBusStops : BusStopsScreenState() {
        object Fetching : StarredBusStops()

        data class Success(val listItems: List<BusStopsItemData>) : StarredBusStops()
    }
}

enum class StopsFilter {
    BUS_STOPS_ONLY,
    TRAIN_STOPS_ONLY
}