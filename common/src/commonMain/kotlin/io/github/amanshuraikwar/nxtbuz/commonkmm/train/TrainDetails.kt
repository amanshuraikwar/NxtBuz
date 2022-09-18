package io.github.amanshuraikwar.nxtbuz.commonkmm.train

data class TrainDetails(
    val trainCode: String,
    val trainCategoryName: String,
    val sourceTrainStopName: String,
    val destinationTrainStopName: String,
    val facilities: List<TrainFacility>,
    val rollingStock: List<TrainRollingStock>,
    val length: Int,
    val lengthInMeters: Int,
    val route: List<TrainRouteNode>
)

data class TrainRouteNode(
    val index: Int,
    val trainStopCode: String,
    // this will be set to false for stops outside of the NL
    val supportedStop: Boolean,
    val trainStopName: String,
    val countryCode: String,
    val type: TrainRouteNodeType,
    val crowdStatus: TrainCrowdStatus,
)

enum class TrainCrowdStatus {
    MEDIUM, LOW, UNKNOWN, HIGH
}

sealed class TrainRouteNodeTiming {
    data class Available(
        val plannedTime: String,
        val actualTime: String?,
        val delayedByMinutes: Int,
        val plannedTrack: String,
        val actualTrack: String?,
        val cancelled: Boolean,
    ) : TrainRouteNodeTiming()

    object NoData : TrainRouteNodeTiming()
}

sealed class TrainRouteNodeType {
    data class Origin(
        val departureTiming: TrainRouteNodeTiming,
    ) : TrainRouteNodeType()

    object Passing : TrainRouteNodeType()

    data class Stop(
        val arrivalTiming: TrainRouteNodeTiming,
        val departureTiming: TrainRouteNodeTiming,
    ) : TrainRouteNodeType()

    data class Destination(
        val arrivalTiming: TrainRouteNodeTiming,
    ) : TrainRouteNodeType()
}

data class TrainRollingStock(
    val type: String,
    val facilities: List<TrainFacility>,
    val imageUrl: String?,
    val width: Int?,
    val height: Int?
)

enum class TrainFacility {
    TOILET, POWER_SOCKETS, WIFI, QUIET_TRAIN, BICYCLE, WHEELCHAIR_ACCESSIBLE
}