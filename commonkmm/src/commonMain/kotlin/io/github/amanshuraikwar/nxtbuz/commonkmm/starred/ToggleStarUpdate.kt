package io.github.amanshuraikwar.nxtbuz.commonkmm.starred

data class ToggleStarUpdate(
    val busStopCode: String,
    val busServiceNumber: String,
    val newStarState: Boolean,
)