package io.github.amanshuraikwar.nxtbuz.ui.model

import io.github.amanshuraikwar.nxtbuz.common.model.BusStop

sealed class MainScreenState(
    val searchVisible: Boolean
) {
    class BusStops(
        searchVisible: Boolean
    ) : MainScreenState(searchVisible)

    class BusStopArrivals(
        searchVisible: Boolean,
        val busStop: BusStop
    ) : MainScreenState(searchVisible)

    class BusRoute(
        searchVisible: Boolean,
        val busStopCode: String,
        val busServiceNumber: String,
    ) : MainScreenState(searchVisible)

}

fun MainScreenState.BusRoute.copy(
    searchVisible: Boolean
): MainScreenState.BusRoute {
    return MainScreenState.BusRoute(
        searchVisible = searchVisible,
        busServiceNumber = busServiceNumber,
        busStopCode = busStopCode
    )
}

fun MainScreenState.BusStopArrivals.copy(
    searchVisible: Boolean
): MainScreenState.BusStopArrivals {
    return MainScreenState.BusStopArrivals(
        searchVisible = searchVisible,
        busStop = busStop,
    )
}