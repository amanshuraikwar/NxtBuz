package io.github.amanshuraikwar.nxtbuz.iosumbrella.model

sealed class IosBusStopArrivalOutput {
    data class Success(val busStopArrivalList: List<IosBusStopArrival>) : IosBusStopArrivalOutput()
    data class Error(val errorMessage: String) : IosBusStopArrivalOutput()
}