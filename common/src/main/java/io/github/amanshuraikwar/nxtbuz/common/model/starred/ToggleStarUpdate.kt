package io.github.amanshuraikwar.nxtbuz.common.model.starred

data class ToggleStarUpdate(
    val busStopCode: String,
    val busServiceNumber: String,
    val newStarState: Boolean,
)