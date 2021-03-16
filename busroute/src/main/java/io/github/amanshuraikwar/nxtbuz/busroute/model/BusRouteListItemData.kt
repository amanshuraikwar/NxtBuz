package io.github.amanshuraikwar.nxtbuz.busroute.model

import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals

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
        val busStopCode: String,
        val busStopDescription: String,
        val position: Position,
        val arrivalState: ArrivalState,
    ) : BusRouteListItemData() {

        enum class Position { ORIGIN, DESTINATION, MIDDLE }

        class Current(
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Fetching,
        ) : BusRouteNode(busStopCode, busStopDescription, position, arrivalState) {
            fun copy(arrivalState: ArrivalState): Current {
                return Current(
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState
                )
            }
        }

        class Previous(
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            // TODO: 16/03/21 arrival state
        ) : BusRouteNode(busStopCode, busStopDescription, position, ArrivalState.Inactive)

        class Next(
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Inactive,
        ) : BusRouteNode(busStopCode, busStopDescription, position, arrivalState) {
            fun copy(
                arrivalState: ArrivalState,
            ): Next {
                return Next(
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState,
                )
            }
        }
    }

    sealed class ArrivalState {
        object Inactive : ArrivalState()
        object Fetching : ArrivalState()
        data class Active(
            val arrivals: Arrivals,
            val lastUpdatedOn: String,
        ) : ArrivalState()
    }
}