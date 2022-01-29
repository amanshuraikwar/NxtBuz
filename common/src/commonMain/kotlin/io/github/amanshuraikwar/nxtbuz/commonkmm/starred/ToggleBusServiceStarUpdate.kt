package io.github.amanshuraikwar.nxtbuz.commonkmm.starred

data class ToggleBusServiceStarUpdate(
    val busStopCode: String,
    val busServiceNumber: String,
    val newStarState: Boolean,
)