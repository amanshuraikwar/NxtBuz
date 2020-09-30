package io.github.amanshuraikwar.nxtbuz.common.model

data class StarToggleState(
    val busStopCode: String,
    val busServiceNumber: String,
    val newToggleState: Boolean
)