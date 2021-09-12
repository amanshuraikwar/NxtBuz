package io.github.amanshuraikwar.nxtbuz.localdatasource

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class LocalHourMinute(
    val hour: Int,
    val minute: Int
) {
    override fun toString(): String {
        return "LocalHourMinute(hour=$hour, minute=$minute)"
    }
}

fun String.toLocalHourMinute(): LocalHourMinute {
    return Regex("[0-9]+").findAll(this).asIterable().toList().let {
        LocalHourMinute(
            it.getOrNull(0)?.value?.toIntOrNull() ?: 0,
            it.getOrNull(1)?.value?.toIntOrNull() ?: 0
        )
    }
}

fun LocalHourMinute.isBefore(instant: Instant): Boolean {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return when {
        hour < localDateTime.hour -> {
            true
        }
        hour == localDateTime.hour -> {
            minute < localDateTime.minute
        }
        else -> {
            false
        }
    }
}

fun LocalHourMinute.isAfter(instant: Instant): Boolean {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return when {
        hour > localDateTime.hour -> {
            true
        }
        hour == localDateTime.hour -> {
            minute > localDateTime.minute
        }
        else -> {
            false
        }
    }
}