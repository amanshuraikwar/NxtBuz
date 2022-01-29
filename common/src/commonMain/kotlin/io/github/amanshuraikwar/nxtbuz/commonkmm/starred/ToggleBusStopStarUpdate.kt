package io.github.amanshuraikwar.nxtbuz.commonkmm.starred

data class ToggleBusStopStarUpdate(
    val busStopCode: String,
    val newStarState: Boolean,
)