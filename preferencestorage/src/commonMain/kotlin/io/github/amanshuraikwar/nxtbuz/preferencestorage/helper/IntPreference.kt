package io.github.amanshuraikwar.nxtbuz.preferencestorage.helper

import com.russhwolf.settings.Settings
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class IntPreference(
    private val preferences: Lazy<Settings>,
    private val name: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.value.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) =
        preferences.value.putInt(name, value)
}