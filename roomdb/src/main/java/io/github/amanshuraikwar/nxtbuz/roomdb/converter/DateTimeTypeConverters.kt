package io.github.amanshuraikwar.nxtbuz.roomdb.converter

import androidx.room.TypeConverter
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalHourMinute
import io.github.amanshuraikwar.nxtbuz.localdatasource.toLocalHourMinute
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal object DateTimeTypeConverters {
    @TypeConverter
    @JvmStatic
    fun stringToLocalDateTime(a: String?): LocalDateTime? {
        return a?.toLocalDateTime()
    }

    @TypeConverter
    @JvmStatic
    fun localDateTimeToString(a: LocalDateTime?): String? {
        return a?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun stringToInstant(a: String?): Instant? {
        return a?.toInstant()
    }

    @TypeConverter
    @JvmStatic
    fun instantToString(a: Instant?): String? {
        return a?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun stringToLocalHourMinute(a: String?): LocalHourMinute? {
        return a?.toLocalHourMinute()
    }

    @TypeConverter
    @JvmStatic
    fun localHourMinuteToString(a: LocalHourMinute?): String? {
        return a?.toString()
    }
}