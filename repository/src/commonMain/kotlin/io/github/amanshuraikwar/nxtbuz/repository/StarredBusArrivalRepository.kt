package io.github.amanshuraikwar.nxtbuz.repository

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleStarUpdate
import kotlinx.coroutines.flow.SharedFlow

interface StarredBusArrivalRepository {
    val toggleStarUpdate: SharedFlow<ToggleStarUpdate>
    val toggleShouldShowErrorArrivals: SharedFlow<Boolean>

    suspend fun shouldShowErrorStarredBusArrivals(): Boolean

    suspend fun setShouldShowErrorStarredBusArrivals(shouldShow: Boolean)

    suspend fun getStarredBusServices(): List<StarredBusService>

    suspend fun toggleBusStopStar(busStopCode: String, busServiceNumber: String)

    suspend fun toggleBusStopStar(
        busStopCode: String,
        busServiceNumber: String,
        toggleTo: Boolean
    )

    suspend fun isStarred(
        busStopCode: String,
        busServiceNumber: String,
    ): Boolean
}