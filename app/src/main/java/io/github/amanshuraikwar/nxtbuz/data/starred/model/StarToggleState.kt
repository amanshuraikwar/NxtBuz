package io.github.amanshuraikwar.nxtbuz.data.starred.model

data class StarToggleState(
    val busStopCode: String,
    val busServiceNumber: String,
    val newToggleState: Boolean
)