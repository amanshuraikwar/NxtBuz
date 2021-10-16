package io.github.amanshuraikwar.nxtbuz.preferencestorage

import com.russhwolf.settings.Settings
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.preferencestorage.helper.*

internal class PreferenceStorageImpl(
    private val settingsFactory: () -> Settings = { Settings() }
) : PreferenceStorage {
    private val settings: Lazy<Settings> = lazy {
        settingsFactory()
    }

    override var onboardingCompleted by BooleanPreference(
        settings,
        PREF_ONBOARDING,
        false
    )

    override var busStopsQueryLimit by IntPreference(
        settings, PREF_BUS_STOPS_QUERY_LIMIT, 30
    )

    override var defaultLocation by ObjectPreference(
        settings,
        PREF_DEFAULT_LOCATION,
        1.3416 to 103.7757,
        { (lat, lon) -> "$lat-$lon" },
        { str -> str?.split("-")?.let { it[0].toDouble() to it[1].toDouble() } }
    )

    override var maxDistanceOfClosestBusStop by IntPreference(
        settings, PREF_MAX_DISTANCE_OF_CLOSEST_BUS_STOP, 50_000
    )

    override var showErrorStarredBusArrivals by BooleanPreference(
        settings, PREF_SHOW_ERROR_STARRED_BUS_ARRIVALS, true
    )

    override var alertStarredBusArrivals by BooleanPreference(
        settings, PREF_ALERT_STARRED_BUS_ARRIVALS, true
    )

    override var alertStarredBusArrivalsMinutes by IntPreference(
        settings, PREF_ALERT_STARRED_BUS_ARRIVALS_MINUTES, 5
    )

    override var alertStarredBusArrivalsFrequency by ObjectPreference(
        settings,
        PREF_ALERT_STARRED_BUS_ARRIVALS_FREQUENCY,
        io.github.amanshuraikwar.nxtbuz.commonkmm.AlertFrequency.ONCE,
        { it.toString() },
        fromStr@{ str ->
            io.github.amanshuraikwar.nxtbuz.commonkmm.AlertFrequency.valueOf(
                str ?: return@fromStr null
            )
        }
    )

    override var showMap by BooleanPreference(
        settings, PREF_SHOW_MAP, false
    )

    override var permissionDeniedPermanently by BooleanPreference(
        settings, PREF_PERMISSION_DENIED_PERMANENTLY, false
    )

    override var theme: NxtBuzTheme by ObjectPreference(
        settings,
        PREF_THEME,
        NxtBuzTheme.DARK,
        { it.toString() },
        fromStr@{ str ->
            NxtBuzTheme.valueOf(
                str ?: return@fromStr null
            )
        }
    )

    override var useSystemTheme by BooleanPreference(
        settings, PREF_USE_SYSTEM_THEME, true
    )

    override var playStoreReviewTimeMillis by LongPreference(
        settings, PREF_SETUP_COMPLETE_TIME_MILLIS, -1
    )

    override var homeBusStopCode: String by StringPreference(
        settings, PREF_HOME_BUS_STOP_CODE, ""
    )

    companion object {
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
        const val PREF_SETUP_COMPLETE_TIME_MILLIS = "pref_setup_complete_time_millis"
        const val PREF_HOME_BUS_STOP_CODE = "pref_home_bus_stop_code"
    }
}