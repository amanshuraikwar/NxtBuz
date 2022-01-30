package io.github.amanshuraikwar.nxtbuz.busstop.busstops.model

sealed class BusStopsScreenState {
    object Fetching : BusStopsScreenState()

    object Failed : BusStopsScreenState()

    sealed class NearbyBusStops : BusStopsScreenState() {
        object Fetching : NearbyBusStops()

        data class LocationError(
            val title: String,
            val primaryButtonText: String,
            val onPrimaryButtonClick: () -> Unit,
            val secondaryButtonText: String,
            val onSecondaryButtonClick: () -> Unit
        ) : NearbyBusStops()

        data class Success(val listItems: List<BusStopsItemData>) : NearbyBusStops()
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