package io.github.amanshuraikwar.nxtbuz.iosumbrella.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusArrival

sealed class IosStarredBusArrivalOutput {
    data class Success(
        val starredBusArrivalList: List<StarredBusArrival>
        ) : IosStarredBusArrivalOutput()
    data class Error(val errorMessage: String) : IosStarredBusArrivalOutput()
}