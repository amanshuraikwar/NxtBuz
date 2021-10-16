package io.github.amanshuraikwar.nxtbuz.preferencestorage.helper

import com.russhwolf.settings.Settings
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class ObjectPreference<T>(
    private val preferences: Lazy<Settings>,
    private val name: String,
    private val defaultValue: T,
    private val toString: (T) -> String,
    private val fromString: (String?) -> T?
) : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return preferences
            .value
            .getString(
                name,
                toString(defaultValue)
            )
            .let {
                fromString(it) ?: defaultValue
            }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        preferences.value.putString(name, toString(value))
    }
}