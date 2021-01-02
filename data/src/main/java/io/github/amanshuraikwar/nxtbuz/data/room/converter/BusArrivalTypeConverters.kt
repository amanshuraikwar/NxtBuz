package io.github.amanshuraikwar.nxtbuz.data.room.converter

import androidx.room.TypeConverter
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrivalStatus
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType

object BusArrivalTypeConverters {

    @TypeConverter
    @JvmStatic
    fun a(a: String?): BusArrivalStatus? {
        return a?.let {
            return BusArrivalStatus.valueOf(a)
        }
    }

    @TypeConverter
    @JvmStatic
    fun b(a: BusArrivalStatus?): String? {
        return a?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun c(a: String?): BusLoad? {
        return a?.let {
            return BusLoad.valueOf(a)
        }
    }

    @TypeConverter
    @JvmStatic
    fun d(a: BusLoad?): String? {
        return a?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun e(a: String?): BusType? {
        return a?.let {
            return BusType.valueOf(a)
        }
    }

    @TypeConverter
    @JvmStatic
    fun f(a: BusType?): String? {
        return a?.toString()
    }
}