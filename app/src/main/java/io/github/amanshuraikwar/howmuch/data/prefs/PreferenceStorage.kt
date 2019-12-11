package io.github.amanshuraikwar.howmuch.data.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.threeten.bp.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var onboardingCompleted: Boolean
    var zoneId: ZoneId
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
@Singleton
class SharedPreferenceStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val observableSelectedThemeResult = MutableLiveData<String>()

    private val changeListener = OnSharedPreferenceChangeListener { _, _ ->
    }

    override var onboardingCompleted by BooleanPreference(prefs, PREF_ONBOARDING, false)

    override var zoneId by ObjectPreference<ZoneId>(
        prefs,
        PREF_ZONE_ID,
        ZoneId.systemDefault(),
        { Gson().toJson(PrefsZoneId(it)) },
        {
            // todo fix
            PrefsZoneId().zoneId
//            if (it != null) {
//                Gson().fromJson(it, PrefsZoneId::class.java).zoneId
//            } else {
//                PrefsZoneId().zoneId
//            }
        }
    )

    companion object {
        const val PREFS_NAME = "howmuch"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_ZONE_ID = "pref_zone_id"
    }

    fun registerOnPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        prefs.value.registerOnSharedPreferenceChangeListener(listener)
    }
}

data class PrefsZoneId(val zoneId: ZoneId = ZoneId.systemDefault())

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
