package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource

import io.github.amanshuraikwar.nxtbuz.remotedatasource.ArrivingBusItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusArrivalItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusArrivalsResponseDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusRouteItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusStopItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class KtorRemoteDataSource internal constructor(
    private val ltaApi: LtaBusApi,
    private val ioDispatcher: CoroutineDispatcher
) : RemoteDataSource {
    override suspend fun getBusStops(skip: Int): List<BusStopItemDto> {
        return withContext(ioDispatcher) {
            ltaApi
                .getBusStops(skip = skip)
                .let { busStopsResponse ->
                    busStopsResponse.busStops.map { busStopItem ->
                        BusStopItemDto(
                            code = busStopItem.code,
                            roadName = busStopItem.roadName,
                            description = busStopItem.description,
                            lat = busStopItem.latitude,
                            lng = busStopItem.longitude
                        )
                    }
                }
        }
    }

    override suspend fun getBusArrivals(
        busStopCode: String,
        busServiceNumber: String?
    ): BusArrivalsResponseDto {
        return withContext(ioDispatcher) {
            ltaApi
                .getBusArrivals(
                    busStopCode = busStopCode,
                    busServiceNumber = busServiceNumber,
                )
                .let { busArrivalsResponse ->
                    BusArrivalsResponseDto(
                        busStopCode = busArrivalsResponse.busStopCode,
                        busArrivals = busArrivalsResponse
                            .busArrivals
                            .map { busArrivalItem ->
                                BusArrivalItemDto(
                                    serviceNumber = busArrivalItem.serviceNumber,
                                    operator = busArrivalItem.operator,
                                    arrivingBus = busArrivalItem
                                        .arrivingBus
                                        ?.let { arrivingBusItem ->
                                            ArrivingBusItemDto(
                                                originCode = arrivingBusItem.originCode,
                                                destinationCode = arrivingBusItem.destinationCode,
                                                estimatedArrival = arrivingBusItem.estimatedArrival,
                                                lat = arrivingBusItem.latitude,
                                                lng = arrivingBusItem.longitude,
                                                visitNumber = arrivingBusItem.visitNumber,
                                                load = arrivingBusItem.load,
                                                feature = arrivingBusItem.feature,
                                                type = arrivingBusItem.type
                                            )
                                        },
                                    arrivingBus1 = busArrivalItem
                                        .arrivingBus1
                                        ?.let { arrivingBusItem ->
                                            ArrivingBusItemDto(
                                                originCode = arrivingBusItem.originCode,
                                                destinationCode = arrivingBusItem.destinationCode,
                                                estimatedArrival = arrivingBusItem.estimatedArrival,
                                                lat = arrivingBusItem.latitude,
                                                lng = arrivingBusItem.longitude,
                                                visitNumber = arrivingBusItem.visitNumber,
                                                load = arrivingBusItem.load,
                                                feature = arrivingBusItem.feature,
                                                type = arrivingBusItem.type
                                            )
                                        },
                                    arrivingBus2 = busArrivalItem
                                        .arrivingBus2
                                        ?.let { arrivingBusItem ->
                                            ArrivingBusItemDto(
                                                originCode = arrivingBusItem.originCode,
                                                destinationCode = arrivingBusItem.destinationCode,
                                                estimatedArrival = arrivingBusItem.estimatedArrival,
                                                lat = arrivingBusItem.latitude,
                                                lng = arrivingBusItem.longitude,
                                                visitNumber = arrivingBusItem.visitNumber,
                                                load = arrivingBusItem.load,
                                                feature = arrivingBusItem.feature,
                                                type = arrivingBusItem.type
                                            )
                                        }
                                )
                            }
                    )
                }
        }
    }

    override suspend fun getBusRoutes(skip: Int): List<BusRouteItemDto> {
        return withContext(ioDispatcher) {
            ltaApi
                .getBusRoutes(skip = skip)
                .let { busRoutesResponse ->
                    busRoutesResponse
                        .busRouteList
                        .map { busRouteItem ->
                            BusRouteItemDto(
                                serviceNumber = busRouteItem.serviceNumber,
                                operator = busRouteItem.operator,
                                direction = busRouteItem.direction,
                                stopSequence = busRouteItem.stopSequence,
                                busStopCode = busRouteItem.busStopCode,
                                distance = busRouteItem.distance ?: 0.0,
                                wdFirstBus = busRouteItem.wdFirstBus,
                                wdLastBus = busRouteItem.wdLastBus,
                                satFirstBus = busRouteItem.satFirstBus,
                                satLastBus = busRouteItem.satLastBus,
                                sunFirstBus = busRouteItem.sunFirstBus,
                                sunLastBus = busRouteItem.sunLastBus,
                            )
                        }
                }
        }
    }

    companion object {
        fun createInstance(
            ltaAccountKey: String,
            addLoggingInterceptors: Boolean,
            ioDispatcher: CoroutineDispatcher
        ): RemoteDataSource {
            return KtorRemoteDataSource(
                ltaApi = LtaBusApi
                    .createInstance(
                        addLoggingInterceptors = addLoggingInterceptors,
                        ltaAccountKey = ltaAccountKey,
                    ),
                ioDispatcher = ioDispatcher
            )
        }

        fun createInstance(
            engine: HttpClientEngine,
            ltaAccountKey: String,
            addLoggingInterceptors: Boolean,
            ioDispatcher: CoroutineDispatcher
        ): RemoteDataSource {
            return KtorRemoteDataSource(
                ltaApi = LtaBusApi
                    .createInstance(
                        engine = engine,
                        addLoggingInterceptors = addLoggingInterceptors,
                        ltaAccountKey = ltaAccountKey,
                    ),
                ioDispatcher = ioDispatcher
            )
        }
    }
}