package io.github.amanshuraikwar.nxtbuz.busroute.model

sealed class BusRouteListItemData {
    data class Header(val title: String) : BusRouteListItemData()

    data class BusRouteHeader(
        val busServiceNumber: String,
        val destinationBusStopDescription: String,
        val originBusStopDescription: String,
    ) : BusRouteListItemData() {
        companion object {
            operator fun invoke() = BusRouteHeader(
                busServiceNumber = "961M",
                destinationBusStopDescription = "Bedok Int",
                originBusStopDescription = "From Juron East Int",
            )
        }
    }

    data class BusRoutePreviousAll(val title: String) : BusRouteListItemData()

    sealed class BusRouteNode(
        val busStopDescription: String,
        val position: Position
    ) : BusRouteListItemData() {

        enum class Position { ORIGIN, DESTINATION, MIDDLE }

        class Current(busStopDescription: String, position: Position = Position.MIDDLE) :
            BusRouteNode(busStopDescription, position)

        class Previous(busStopDescription: String, position: Position = Position.MIDDLE) :
            BusRouteNode(busStopDescription, position)

        class Next(busStopDescription: String, position: Position = Position.MIDDLE) :
            BusRouteNode(busStopDescription, position)
    }
}