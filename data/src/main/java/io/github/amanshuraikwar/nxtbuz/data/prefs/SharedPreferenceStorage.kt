package io.github.amanshuraikwar.nxtbuz.data.prefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import io.github.amanshuraikwar.nxtbuz.common.model.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.data.prefs.helper.*
import javax.inject.Inject
import javax.inject.Singleton

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

    override var showErrorStarredBusArrivals by BooleanPreference(
        prefs, PREF_SHOW_ERROR_STARRED_BUS_ARRIVALS, true
    )

    override var alertStarredBusArrivals by BooleanPreference(
        prefs, PREF_ALERT_STARRED_BUS_ARRIVALS, true
    )

    override var alertStarredBusArrivalsMinutes by IntPreference(
        prefs, PREF_ALERT_STARRED_BUS_ARRIVALS_MINUTES, 5
    )

    override var alertStarredBusArrivalsFrequency by ObjectPreference(
        prefs,
        PREF_ALERT_STARRED_BUS_ARRIVALS_FREQUENCY,
        AlertFrequency.ONCE,
        { it.toString() },
        fromStr@{ str -> AlertFrequency.valueOf(str ?: return@fromStr null) }
    )

    override var showMap by BooleanPreference(
        prefs, PREF_SHOW_MAP, true
    )

    override var permissionDeniedPermanently by BooleanPreference(
        prefs, PREF_PERMISSION_DENIED_PERMANENTLY, false
    )

    override var theme: NxtBuzTheme by ObjectPreference(
        prefs,
        PREF_THEME,
        NxtBuzTheme.DARK,
        { it.toString() },
        fromStr@{ str -> NxtBuzTheme.valueOf(str ?: return@fromStr null) }
    )

    override var useSystemTheme by BooleanPreference(
        prefs, PREF_USE_SYSTEM_THEME, true
    )

    companion object {
        const val PREFS_NAME = "io.github.amanshuraikwar.nxtbuz"
        const val PREF_ONBOARDING = "pref_onboarding"
        const val PREF_BUS_STOPS_QUERY_LIMIT = "pref_bus_stops_query_limit"
        const val PREF_DEFAULT_LOCATION = "pref_default_location"
        const val PREF_MAX_DISTANCE_OF_CLOSEST_BUS_STOP = "pref_max_distance_of_closest_bus_stop"
        const val PREF_SHOW_ERROR_STARRED_BUS_ARRIVALS = "show_error_bus_arrivals"
        const val PREF_ALERT_STARRED_BUS_ARRIVALS = "alert_starred_bus_arrivals"
        const val PREF_ALERT_STARRED_BUS_ARRIVALS_MINUTES = "alert_starred_bus_arrivals_minutes"
        const val PREF_ALERT_STARRED_BUS_ARRIVALS_FREQUENCY = "alert_starred_bus_arrivals_frequency"
        const val PREF_SHOW_MAP = "show_map"
        const val PREF_PERMISSION_DENIED_PERMANENTLY = "permission_denied_permanently"
        const val PREF_THEME = "pref_theme"
        const val PREF_USE_SYSTEM_THEME = "pref_use_system_theme"
    }
}