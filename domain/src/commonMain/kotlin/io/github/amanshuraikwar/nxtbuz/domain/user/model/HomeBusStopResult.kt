package io.github.amanshuraikwar.nxtbuz.domain.user.model

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop

sealed class HomeBusStopResult {
    object NotSet : HomeBusStopResult()
    data class Success(val busStop: BusStop): HomeBusStopResult()
}