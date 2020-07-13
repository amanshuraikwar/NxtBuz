package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.loop

import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival

data class ArrivalsLoopData(
    val busStopCode: String,
    val busServiceNumber: String,
    val busArrival: BusArrival
)