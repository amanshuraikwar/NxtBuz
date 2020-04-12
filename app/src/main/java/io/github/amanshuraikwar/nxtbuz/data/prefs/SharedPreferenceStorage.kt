package io.github.amanshuraikwar.nxtbuz.data.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.github.amanshuraikwar.nxtbuz.BuildConfig
import io.github.amanshuraikwar.nxtbuz.data.prefs.model.BooleanPreference
import io.github.amanshuraikwar.nxtbuz.data.prefs.model.IntPreference
import io.github.amanshuraikwar.nxtbuz.data.prefs.model.ObjectPreference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
@Singleton
class SharedPreferenceStorage @Inject constructor(context: Context) : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override var onboardingCompleted by BooleanPreference(
        prefs,
        PREF_ONBOARDING,
        false
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

    companion object {
        const val PREFS_NAME = BuildConfig.APPLICATION_ID
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_BUS_STOPS_QUERY_LIMIT = "pref_bus_stops_query_limit"
        const val PREF_DEFAULT_LOCATION = "pref_default_location"
        const val PREF_MAX_DISTANCE_OF_CLOSEST_BUS_STOP = "pref_max_distance_of_closest_bus_stop"
    }
}