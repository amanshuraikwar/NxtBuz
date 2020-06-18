package io.github.amanshuraikwar.nxtbuz.data.busarrival.util

import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalEntity
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalStatus
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit

fun BusArrivalEntity.getArrivalTimeStr(): String {

    if (busArrivalStatus == BusArrivalStatus.NO_DATA) {
        return "NO DATA"
    }

    if (busArrivalStatus == BusArrivalStatus.NOT_OPERATING) {
        return "NOT OPR"
    }

    val timeDiff =
        ChronoUnit.MINUTES.between(
            OffsetDateTime.now(),
            estimatedArrivalTimestamp
        )

    return when {
        timeDiff >= 60 -> "60+"
        timeDiff > 0 -> String.format("%02d", timeDiff)
        else -> "Arr"
    }
}

fun BusArrivalEntity.getArrivalTimeStrNotification(): String {

    if (busArrivalStatus == BusArrivalStatus.NO_DATA) {
        return ""
    }

    if (busArrivalStatus == BusArrivalStatus.NOT_OPERATING) {
        return ""
    }

    val timeDiff =
        ChronoUnit.MINUTES.between(
            OffsetDateTime.now(),
            estimatedArrivalTimestamp
        )

    return when {
        timeDiff >= 60 -> "in 60+ Mins"
        timeDiff > 0 -> String.format("in %02d Mins", timeDiff)
        else -> "Now"
    }
}

fun BusArrivalEntity.isArrivingIn(minutes: Int): Boolean {

    if (busArrivalStatus == BusArrivalStatus.NO_DATA) {
        return false
    }

    if (busArrivalStatus == BusArrivalStatus.NOT_OPERATING) {
        return false
    }

    val timeDiff =
        ChronoUnit.MINUTES.between(
            OffsetDateTime.now(),
            estimatedArrivalTimestamp
        )

    return timeDiff <= minutes
}

fun OffsetDateTime.asArrivingInMin(): Int {

    val timeDiff =
        ChronoUnit.MINUTES.between(
            OffsetDateTime.now(),
            this
        )

    return 0.coerceAtLeast(timeDiff.toInt())
}

fun OffsetDateTime.asArrivingInMinutesStr(): String {

    val timeDiff =
        ChronoUnit.MINUTES.between(
            OffsetDateTime.now(),
            this
        )

    return when {
        timeDiff >= 60 -> "in 60+ Mins"
        timeDiff > 1 -> String.format("in %02d Mins", timeDiff)
        timeDiff >= 1 -> String.format("in %02d Min", timeDiff)
        else -> "Now"
    }
}

fun Int.toNotificationTimeStr(): String {
    return when {
        this > 1 -> String.format("in %02d Mins")
        this >= 1 -> String.format("in %02d Min")
        else -> "Now"
    }
}