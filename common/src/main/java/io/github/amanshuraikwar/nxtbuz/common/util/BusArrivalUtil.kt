package io.github.amanshuraikwar.nxtbuz.common.util

fun Int.toArrivalString(): String {
    return when {
        this >= 60 -> "in 60+ mins"
        this > 0 -> String.format("in %02d mins", this)
        else -> "Arriving Now"
    }
}