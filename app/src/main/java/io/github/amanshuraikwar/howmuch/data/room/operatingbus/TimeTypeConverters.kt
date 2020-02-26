package io.github.amanshuraikwar.howmuch.data.room.operatingbus

import androidx.room.TypeConverter
import org.threeten.bp.OffsetTime
import org.threeten.bp.format.DateTimeFormatter

object TimeTypeConverters {

    private val formatter = DateTimeFormatter.ISO_OFFSET_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetTime(value: String?): OffsetTime? {
        return value?.let {
            return formatter.parse(value, OffsetTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetTime(date: OffsetTime?): String? {
        return date?.format(formatter)
    }
}