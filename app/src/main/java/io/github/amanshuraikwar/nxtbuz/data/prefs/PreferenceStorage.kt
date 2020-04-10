package io.github.amanshuraikwar.nxtbuz.data.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.threeten.bp.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var onboardingCompleted: Boolean
    var zoneId: ZoneId
    var busStopsQueryLimit: Int
    var defaultLocation: Pair<Double, Double>
    var maxDistanceOfClosestBusStop: Int
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

    override var busStopsQueryLimit by IntPreference(
        prefs, PREF_BUS_STOPS_QUERY_LIMIT, 30
    )

    override var defaultLocation by ObjectPreference(
        prefs,
        PREF_DEFAULT_LOCATION,
        1.3416 to 103.7757,
        { (lat, lon) -> "$lat-$lon" },
        { str -> str?.split("-")?.let { it[0].toDouble() to it[1].toDouble() } }
    )

    override var maxDistanceOfClosestBusStop by IntPreference(
        prefs, PREF_MAX_DISTANCE_OF_CLOSEST_BUS_STOP, 50_000
    )

    fun registerOnPreferenceChangeListener(listener: OnSharedPreferenceChangeListener) {
        prefs.value.registerOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val PREFS_NAME = "howmuch"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_ZONE_ID = "pref_zone_id"
        const val PREF_BUS_STOPS_QUERY_LIMIT = "bus_stops_query_limit"
        const val PREF_DEFAULT_LOCATION = "default_location"
        const val PREF_MAX_DISTANCE_OF_CLOSEST_BUS_STOP = "max_distance_of_closest_bus_stop"

    }

    data class PrefsZoneId(val zoneId: ZoneId = ZoneId.systemDefault())
}


