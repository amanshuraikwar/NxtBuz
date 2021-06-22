package io.github.amanshuraikwar.nxtbuz.busstop.busstops.model

sealed class BusStopsScreenState {
    object Fetching : BusStopsScreenState()

    object Failed : BusStopsScreenState()

    data class LocationError(
        val title: String,
        val primaryButtonText: String,
        val onPrimaryButtonClick: () -> Unit,
        val secondaryButtonText: String,
        val onSecondaryButtonClick: () -> Unit
    ) : BusStopsScreenState()

    data class Success(val listItems: List<BusStopsItemData>) : BusStopsScreenState()
}