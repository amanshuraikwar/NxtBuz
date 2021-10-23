package io.github.amanshuraikwar.nxtbuz.commonkmm

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeUtil {
    fun isWeekday(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.dayOfWeek == DayOfWeek.MONDAY
                || now.dayOfWeek == DayOfWeek.TUESDAY
                || now.dayOfWeek == DayOfWeek.WEDNESDAY
                || now.dayOfWeek == DayOfWeek.THURSDAY
                || now.dayOfWeek == DayOfWeek.FRIDAY
    }

    fun isSaturday(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.dayOfWeek == DayOfWeek.SATURDAY
    }

    fun isSunday(): Boolean {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return now.dayOfWeek == DayOfWeek.SUNDAY
    }
}