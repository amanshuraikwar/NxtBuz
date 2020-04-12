package io.github.amanshuraikwar.nxtbuz.data.prefs

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var onboardingCompleted: Boolean
    var busStopsQueryLimit: Int
    var defaultLocation: Pair<Double, Double>
    var maxDistanceOfClosestBusStop: Int
}


