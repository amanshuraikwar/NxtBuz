package io.github.amanshuraikwar.nsapi

import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusArrivalsResponseDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusRouteItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusStopItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.NearbyMeLocationIdDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.remotedatasource.StationItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.StationNameDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class NsApiRemoteDataSource internal constructor(
    private val nsApi: NsApi,
    private val ioDispatcher: CoroutineDispatcher
) : RemoteDataSource {
    override suspend fun getBusStops(skip: Int): List<BusStopItemDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getBusArrivals(
        busStopCode: String,
        busServiceNumber: String?
    ): BusArrivalsResponseDto {
        TODO("Not yet implemented")
    }

    override suspend fun getBusRoutes(skip: Int): List<BusRouteItemDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrainStations(skip: Int): List<StationItemDto> {
        return withContext(ioDispatcher) {
            nsApi.getStations().payload.map { stationDto ->
                StationItemDto(
                    stationType = stationDto.stationType,
                    code = stationDto.code,
                    hasFacilities = stationDto.heeftFaciliteiten,
                    hasDepartureTimes = stationDto.heeftVertrektijden,
                    hasTravelAssistance = stationDto.heeftReisassistentie,
                    names = StationNameDto(
                        long = stationDto.namen.lang,
                        medium = stationDto.namen.middel,
                        short = stationDto.namen.kort
                    ),
                    land = stationDto.land,
                    lat = stationDto.lat,
                    lng = stationDto.lng,
                    entryDate = stationDto.ingangsDatum,
                    nearbyMeLocationId = NearbyMeLocationIdDto(
                        type = stationDto.nearbyMeLocationId.type,
                        value = stationDto.nearbyMeLocationId.value
                    )
                )
            }
        }
    }

    companion object {
        fun createInstance(
            subscriptionKey: String,
            addLoggingInterceptors: Boolean,
            ioDispatcher: CoroutineDispatcher
        ): RemoteDataSource {
            return NsApiRemoteDataSource(
                nsApi = NsApi
                    .createInstance(
                        addLoggingInterceptors = addLoggingInterceptors,
                        subscriptionKey = subscriptionKey,
                    ),
                ioDispatcher = ioDispatcher
            )
        }
    }
}