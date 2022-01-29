package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleBusServiceStarUpdate
import kotlinx.coroutines.flow.SharedFlow

interface StarredBusArrivalRepository {
    val toggleBusServiceStarUpdate: SharedFlow<ToggleBusServiceStarUpdate>
    val toggleShouldShowErrorArrivals: SharedFlow<Boolean>

    suspend fun shouldShowErrorStarredBusArrivals(): Boolean

    suspend fun setShouldShowErrorStarredBusArrivals(shouldShow: Boolean)

    suspend fun getStarredBusServices(atBusStopCode: String? = null): List<StarredBusService>

    suspend fun toggleBusServiceStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean? = null
    )

    suspend fun isBusServiceStarred(
        busStopCode: String,
        busServiceNumber: String,
    ): Boolean
}