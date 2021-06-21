package io.github.amanshuraikwar.nxtbuz.data.prefs.helper

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ObjectPreference<T>(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: T,
    private val toString: (T) -> String,
    private val fromString: (String?) -> T?
) : ReadWriteProperty<Any, T> {
    @WorkerThread
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
        preferences.value.edit { putString(name, toString(value)) }
    }
}