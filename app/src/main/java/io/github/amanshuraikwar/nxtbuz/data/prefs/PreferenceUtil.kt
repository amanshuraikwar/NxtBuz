package io.github.amanshuraikwar.nxtbuz.data.prefs

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}

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

class IntPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.value.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) =
        preferences.value.edit { putInt(name, value) }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
        preferences.value.edit { putBoolean(name, value) }
}