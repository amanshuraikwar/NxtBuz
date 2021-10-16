package io.github.amanshuraikwar.nxtbuz.preferencestorage.helper

import com.russhwolf.settings.Settings
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("unused")
internal class StringPreference(
    private val preferences: Lazy<Settings>,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        preferences.value.putString(name, value)
    }
}