package io.github.amanshuraikwar.nxtbuz.data.prefs

import io.github.amanshuraikwar.nxtbuz.common.model.AlertFrequency

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var onboardingCompleted: Boolean
    var busStopsQueryLimit: Int
    var defaultLocation: Pair<Double, Double>
    var maxDistanceOfClosestBusStop: Int
    var showErrorStarredBusArrivals: Boolean
    var alertStarredBusArrivals: Boolean
    var alertStarredBusArrivalsMinutes: Int
    var alertStarredBusArrivalsFrequency: AlertFrequency
    var showMap: Boolean
}


