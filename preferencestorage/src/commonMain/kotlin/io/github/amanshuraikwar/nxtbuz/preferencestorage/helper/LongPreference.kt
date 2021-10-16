package io.github.amanshuraikwar.nxtbuz.preferencestorage.helper

import com.russhwolf.settings.Settings
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class LongPreference(
    private val preferences: Lazy<Settings>,
    private val name: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) =
        preferences.value.putLong(name, value)
}