package io.github.amanshuraikwar.nxtbuz.data.room

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetTime
import org.threeten.bp.format.DateTimeFormatter

object DateTimeTypeConverters {

    private val timeFormatter = DateTimeFormatter.ISO_OFFSET_TIME
    private val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun a(a: String?): OffsetTime? {
        return a?.let {
            return timeFormatter.parse(a, OffsetTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun b(a: OffsetTime?): String? {
        return a?.format(timeFormatter)
    }

    @TypeConverter
    @JvmStatic
    fun c(a: String?): OffsetDateTime? {
        return a?.let {
            return dateTimeFormatter.parse(a, OffsetDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun d(a: OffsetDateTime?): String? {
        return a?.format(dateTimeFormatter)
    }
}