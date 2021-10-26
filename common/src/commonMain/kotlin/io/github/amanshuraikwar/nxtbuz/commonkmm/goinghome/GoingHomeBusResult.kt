package io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome

sealed class GoingHomeBusResult {
    data class TooCloseToHome(
        val homeBusStopCode: String,
        val homeBusStopDescription: String
    ) : GoingHomeBusResult()

    data class NoBusesGoingHome(
        val homeBusStopCode: String,
        val homeBusStopDescription: String
    ) : GoingHomeBusResult()

    object NoBusStopsNearby : GoingHomeBusResult()

    object HomeBusStopNotSet : GoingHomeBusResult()

    data class Processing(
        // 0-100
        val progress: Int,
        val message: String,
    ) : GoingHomeBusResult()

    data class Success(
        val directBuses: List<DirectBus>
    ) : GoingHomeBusResult()
}