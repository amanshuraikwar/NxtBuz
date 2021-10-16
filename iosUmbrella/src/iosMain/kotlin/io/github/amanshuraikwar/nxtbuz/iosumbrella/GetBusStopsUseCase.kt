package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.busstopdata.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetBusStopsUseCase constructor(
    private val busStopRepository: BusStopRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, limit: Int): List<BusStop> {
        return busStopRepository.getCloseBusStops(
            lat = lat,
            lng = lon,
            max = limit,
            maxDistanceMetres = 500
        )
    }

    operator fun invoke(
        lat: Double,
        lon: Double,
        limit: Int,
        callback: (List<BusStop>) -> Unit
    ) {
        IosDataCoroutineScopeProvider.coroutineScope.launch {
            callback(
                busStopRepository.getCloseBusStops(
                    lat = lat,
                    lng = lon,
                    max = limit,
                    maxDistanceMetres = 500
                )
            )
        }
    }

    suspend operator fun invoke(query: String, limit: Int): List<BusStop> {
        return busStopRepository.searchBusStops(query, limit)
    }
}