package io.github.amanshuraikwar.nxtbuz.train.details

import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainCrowdStatus
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainFacility
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNodeType

internal sealed class ScreenState {
    object Fetching : ScreenState()
    data class Success(
        val header: TrainHeader,
        val listItems: List<ListItemData>
    ) : ScreenState()
}

internal data class TrainHeader(
    val trainCode: String,
    val trainCategoryName: String,
    val sourceTrainStopName: String,
    val destinationTrainStopName: String,
    val facilities: List<TrainFacility>,
    val rollingStockImages: List<RollingStockImage>,
    val length: Int,
    val lengthInMeters: Int,
)

internal data class RollingStockImage(
    val imageUrl: String,
    val width: Int?,
    val height: Int?,
    val displayInfo: Boolean
)

internal sealed class ListItemData {
    data class Header(
        val id: String,
        val title: String
    ) : ListItemData()

    data class RouteNodeOrigin(
        val trainStopCode: String,
        val trainStopName: String,
        val crowdStatus: TrainCrowdStatus,
        val type: TrainRouteNodeType.Origin,
    ) : ListItemData()

    data class RouteNodeMiddle(
        val trainStopCode: String,
        val trainStopName: String,
        val crowdStatus: TrainCrowdStatus,
        val type: TrainRouteNodeType.Stop,
    ) : ListItemData()

    data class RouteNodeDestination(
        val trainStopCode: String,
        val trainStopName: String,
        val crowdStatus: TrainCrowdStatus,
        val type: TrainRouteNodeType.Destination,
    ) : ListItemData()
}