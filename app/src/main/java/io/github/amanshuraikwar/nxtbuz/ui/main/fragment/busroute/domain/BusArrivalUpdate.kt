package io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.domain

data class BusArrivalUpdate(
    val busStopCode: String,
    val arrivalList: List<String>,
    val lastUpdateAt: String,
) {
    companion object {
        fun noRadar(busStopCode: String) =
            BusArrivalUpdate(busStopCode, emptyList(), "")
        fun loading(busStopCode: String) =
            BusArrivalUpdate(busStopCode, listOf("Fetching arrivals..."), "")
    }
}