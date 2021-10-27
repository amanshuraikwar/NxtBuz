package io.github.amanshuraikwar.nxtbuz.starred

fun Int.toArrivalString(): String {
    return when {
        this >= 60 -> "60+"
        this > 0 -> String.format("%02d", this)
        else -> "Arr"
    }
}