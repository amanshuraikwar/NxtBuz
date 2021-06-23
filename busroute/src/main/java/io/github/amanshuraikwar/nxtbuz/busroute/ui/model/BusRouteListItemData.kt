package io.github.amanshuraikwar.nxtbuz.busroute.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals

sealed class BusRouteListItemData {
    data class Header(
        val id: String,
        val title: String
    ) : BusRouteListItemData()

    data class BusRoutePreviousAll(
        val id: String,
        val title: String
    ) : BusRouteListItemData()

    sealed class BusRouteNode(
        val id: String,
        val busStopCode: String,
        val busStopDescription: String,
        val position: Position,
        val arrivalState: ArrivalState,
    ) : BusRouteListItemData() {

        enum class Position { ORIGIN, DESTINATION, MIDDLE }

        class Current(
            id: String,
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Fetching,
        ) : BusRouteNode(id, busStopCode, busStopDescription, position, arrivalState) {
            fun copy(arrivalState: ArrivalState): Current {
                return Current(
                    id = id,
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState
                )
            }
        }

        class Previous(
            id: String,
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Inactive,
        ) : BusRouteNode(id, busStopCode, busStopDescription, position, arrivalState) {
            fun copy(arrivalState: ArrivalState): Previous {
                return Previous(
                    id = id,
                    busStopCode = busStopCode,
                    busStopDescription = busStopDescription,
                    position = position,
                    arrivalState = arrivalState
                )
            }
        }

        class Next(
            id: String,
            busStopCode: String,
            busStopDescription: String,
            position: Position = Position.MIDDLE,
            arrivalState: ArrivalState = ArrivalState.Inactive,
        ) : BusRouteNode(id, busStopCode, busStopDescription, position, arrivalState) {
            fun copy(
                arrivalState: ArrivalState,
            ): Next {
                return Next(
                    id = id,
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
            val busArrivals: BusArrivals,
            val lastUpdatedOn: String,
        ) : ArrivalState()
    }
}