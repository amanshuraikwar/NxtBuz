package io.github.amanshuraikwar.nxtbuz.data.busarrival.model

sealed class Arrivals {
    object DataNotAvailable : Arrivals()
    object NotOperating : Arrivals()
    class Arriving(
        val nextArrivingBus: ArrivingBus,
        val followingArrivingBusList: List<ArrivingBus>
    ) : Arrivals()
}