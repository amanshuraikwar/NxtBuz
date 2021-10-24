package io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome

sealed class DirectBusesResult {
    object NoDirectBuses : DirectBusesResult()
    object NotCachedYet : DirectBusesResult()
    data class Success(
        val directBusList: List<DirectBus>
    ) : DirectBusesResult()
}