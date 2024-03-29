package io.github.amanshuraikwar.nxtbuz.commonkmm.arrival

sealed class BusArrivals {
    data class Error(val message: String) : BusArrivals()
    object DataNotAvailable : BusArrivals()
    object NotOperating : BusArrivals()
    class Arriving(
        val nextArrivingBus: ArrivingBus,
        val followingArrivingBusList: List<ArrivingBus>
    ) : BusArrivals()
}