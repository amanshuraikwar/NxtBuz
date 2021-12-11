package io.github.amanshuraikwar.nxtbuz.userdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage

class FakePreferenceStorage : PreferenceStorage {
    override var onboardingCompleted: Boolean = false

    override var busStopsQueryLimit: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var defaultLocation: Pair<Double, Double>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var maxDistanceOfClosestBusStop: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var showErrorStarredBusArrivals: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var alertStarredBusArrivals: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var alertStarredBusArrivalsMinutes: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var alertStarredBusArrivalsFrequency: AlertFrequency
        get() = TODO("Not yet implemented")
        set(value) {}
    override var showMap: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var permissionDeniedPermanently: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override var theme: NxtBuzTheme = NxtBuzTheme.DARK

    override var useSystemTheme: Boolean = true

    override var playStoreReviewTimeMillis: Long = -1L

    override var homeBusStopCode: String
        get() = TODO("Not yet implemented")
        set(value) {}
}