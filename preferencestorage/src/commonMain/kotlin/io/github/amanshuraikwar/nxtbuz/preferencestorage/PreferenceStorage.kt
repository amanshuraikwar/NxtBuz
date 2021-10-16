package io.github.amanshuraikwar.nxtbuz.preferencestorage

import io.github.amanshuraikwar.nxtbuz.commonkmm.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme

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
    var permissionDeniedPermanently: Boolean
    var theme: NxtBuzTheme
    var useSystemTheme: Boolean
    var playStoreReviewTimeMillis: Long
}


