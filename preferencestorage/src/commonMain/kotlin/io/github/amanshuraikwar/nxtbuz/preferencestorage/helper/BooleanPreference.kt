package io.github.amanshuraikwar.nxtbuz.preferencestorage.helper

import com.russhwolf.settings.Settings
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class BooleanPreference(
    private val preferences: Lazy<Settings>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
        preferences.value.putBoolean(name, value)
}