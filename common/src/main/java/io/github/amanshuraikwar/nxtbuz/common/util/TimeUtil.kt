package io.github.amanshuraikwar.nxtbuz.common.util

import org.threeten.bp.DayOfWeek
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

object TimeUtil {

    @JvmStatic
    val TIME_READABLE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    fun isWeekday(): Boolean {
        val now = OffsetDateTime.now()
        return now.dayOfWeek == DayOfWeek.MONDAY
                || now.dayOfWeek == DayOfWeek.TUESDAY
                || now.dayOfWeek == DayOfWeek.WEDNESDAY
                || now.dayOfWeek == DayOfWeek.THURSDAY
                || now.dayOfWeek == DayOfWeek.FRIDAY
    }

    fun isSaturday(): Boolean {
        val now = OffsetDateTime.now()
        return now.dayOfWeek == DayOfWeek.SATURDAY
    }

    fun isSunday(): Boolean {
        val now = OffsetDateTime.now()
        return now.dayOfWeek == DayOfWeek.SUNDAY
    }

    fun currentTimeStr(): String {
        return OffsetDateTime.now().format(TIME_READABLE_FORMATTER)
    }
}