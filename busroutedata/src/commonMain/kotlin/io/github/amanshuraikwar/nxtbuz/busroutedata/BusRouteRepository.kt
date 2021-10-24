package io.github.amanshuraikwar.nxtbuz.busroutedata

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRoute
import io.github.amanshuraikwar.nxtbuz.commonkmm.busroute.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.localdatasource.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalHourMinute
import io.github.amanshuraikwar.nxtbuz.localdatasource.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.remotedatasource.BusRouteItemDto
import io.github.amanshuraikwar.nxtbuz.remotedatasource.RemoteDataSource
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.Clock

class BusRouteRepositoryImpl constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : BusRouteRepository {
    override fun setup(): Flow<Double> = flow {
        emit(0.0)

        localDataSource.deleteAllBusRoutes()
        localDataSource.deleteAllOperatingBuses()

        val startTimeMillis = Clock.System.now().epochSeconds
        val (busStopCodeMap, busServiceNumberMap) = getBusRouteApiOutput(this)

        saveOperatingBusData(busStopCodeMap)

        emit(0.9)

        saveBusRouteData(busServiceNumberMap)

        println(
            "setup: Setup took " +
                    "${(Clock.System.now().epochSeconds - startTimeMillis) / 1000} seconds"
        )

        emit(1.0)
    }.flowOn(dispatcherProvider.computation)

    private data class BusRouteApiOutput(
        val busStopCodeBusRouteItemListMap: Map<String, MutableSet<BusRouteItemDto>>,
        val busServiceNumberBusRouteItemListMap: Map<String, MutableSet<BusRouteItemDto>>
    )

    private suspend fun getBusRouteApiOutput(
        flowCollector: FlowCollector<Double>
    ): BusRouteApiOutput = coroutineScope {
        val busStopCodeBusRouteItemListMap = mutableMapOf<String, MutableSet<BusRouteItemDto>>()
        val busServiceNumberBusRouteItemListMap =
            mutableMapOf<String, MutableSet<BusRouteItemDto>>()

        var skip = 0
        var shouldStop: Boolean

        do {
            val busRouteItemList = remoteDataSource.getBusRoutes(skip)

            busRouteItemList.forEach { busRouteItemDto ->
                busStopCodeBusRouteItemListMap[busRouteItemDto.busStopCode]
                    ?.add(busRouteItemDto)
                    ?: run {
                        busStopCodeBusRouteItemListMap[busRouteItemDto.busStopCode] =
                            mutableSetOf(busRouteItemDto)
                    }

                busServiceNumberBusRouteItemListMap[busRouteItemDto.serviceNumber]
                    ?.add(busRouteItemDto)
                    ?: run {
                        busServiceNumberBusRouteItemListMap[busRouteItemDto.serviceNumber] =
                            mutableSetOf(busRouteItemDto)
                    }
            }

            skip += 500
            flowCollector.emit(
                0.8.coerceAtMost((skip.toDouble() / 32000.0) * 0.8)
            )

            shouldStop = busRouteItemList.isEmpty()
        } while (!shouldStop)

        BusRouteApiOutput(
            busStopCodeBusRouteItemListMap,
            busServiceNumberBusRouteItemListMap
        )
    }

    private suspend fun saveOperatingBusData(
        busStopCodeBusRouteItemListMap: Map<String, MutableSet<BusRouteItemDto>>
    ) = coroutineScope {
        // add all operating buses for every bus stop to local DB
        busStopCodeBusRouteItemListMap
            .map { (busStopCode, busRouteItem) ->
                async(dispatcherProvider.pool8) {
                    localDataSource.insertOperatingBuses(
                        busRouteItem.map {
                            OperatingBusEntity(
                                busStopCode = busStopCode,
                                busServiceNumber = it.serviceNumber,
                                wdFirstBus = if (it.wdFirstBus == "-") {
                                    null
                                } else {
                                    it.wdFirstBus.ltaApiTimeStrToLocalHourMinute()
                                },
                                wdLastBus = if (it.wdLastBus == "-") {
                                    null
                                } else {
                                    it.wdLastBus.ltaApiTimeStrToLocalHourMinute()
                                },
                                satFirstBus = if (it.satFirstBus == "-") {
                                    null
                                } else {
                                    it.satFirstBus.ltaApiTimeStrToLocalHourMinute()
                                },
                                satLastBus = if (it.satLastBus == "-") {
                                    null
                                } else {
                                    it.satLastBus.ltaApiTimeStrToLocalHourMinute()
                                },
                                sunFirstBus = if (it.sunFirstBus == "-") {
                                    null
                                } else {
                                    it.sunFirstBus.ltaApiTimeStrToLocalHourMinute()
                                },
                                sunLastBus = if (it.sunLastBus == "-") {
                                    null
                                } else {
                                    it.sunLastBus.ltaApiTimeStrToLocalHourMinute()
                                }
                            )
                        }
                    )
                }
            }
            .awaitAll()
    }

    private suspend fun saveBusRouteData(
        busServiceNumberBusRouteItemListMap: Map<String, MutableSet<BusRouteItemDto>>
    ) = coroutineScope {
        // store bus route info in local DB
        busServiceNumberBusRouteItemListMap
            .map { (serviceNumber, busRouteItem) ->
                async(dispatcherProvider.pool8) {
                    localDataSource.insertBusRoute(
                        busRouteItem.map {
                            BusRouteEntity(
                                serviceNumber,
                                it.busStopCode,
                                it.direction,
                                it.stopSequence,
                                it.distance
                            )
                        }
                    )
                }
            }
            .awaitAll()
    }

    @Suppress("NAME_SHADOWING")
    override suspend fun getBusRoute(
        busServiceNumber: String,
        direction: Int?,
        busStopCode: String?
    ): BusRoute = withContext(dispatcherProvider.io) {
        val busRouteEntityList = localDataSource.findBusRoute(busServiceNumber = busServiceNumber)

        val direction1 = busRouteEntityList.filter { it.direction == 1 }
        val direction2 = busRouteEntityList.filter { it.direction == 2 }

        val (selectedBusRouteEntityList: List<BusRouteEntity>, direction: Int) = when {
            busRouteEntityList.isEmpty() -> {
                // todo separate error state
                emptyList<BusRouteEntity>() to 0
            }
            direction == 1 -> {
                direction1 to 1
            }
            direction == 2 -> {
                direction2 to 2
            }
            busStopCode != null -> {
                when {
                    direction1.find { it.busStopCode == busStopCode } != null -> {
                        direction1 to 1
                    }
                    direction2.find { it.busStopCode == busStopCode } != null -> {
                        direction2 to 2
                    }
                    else -> {
                        // todo separate error state
                        emptyList<BusRouteEntity>() to 0
                    }
                }
            }
            else -> {
                direction1 to 1
            }
        }

        val busRouteNodeList =
            selectedBusRouteEntityList
                .map { busRouteEntity ->
                    async(dispatcherProvider.pool8) {
                        val busStopEntity =
                            localDataSource.findBusStopByCode(busRouteEntity.busStopCode)
                                ?: throw Exception(
                                    "No bus stop found for bus stop code " +
                                            busRouteEntity.busStopCode
                                )

                        BusRouteNode(
                            busServiceNumber,
                            busStopEntity.code,
                            busRouteEntity.direction,
                            busRouteEntity.stopSequence,
                            busRouteEntity.distance,
                            busStopEntity.roadName,
                            busStopEntity.description,
                            busStopEntity.latitude,
                            busStopEntity.longitude
                        )
                    }
                }
                .awaitAll()
                .sortedBy { it.stopSequence }

        BusRoute(
            busServiceNumber = busServiceNumber,
            direction = direction,
            starred = busStopCode?.let {
                localDataSource.findStarredBuses(busStopCode = busStopCode)
                    .map { it.busServiceNumber }
                    .toSet()
                    .contains(busServiceNumber)
            },
            busRouteNodeList = busRouteNodeList,
            originBusStopDescription = busRouteNodeList.first().busStopDescription,
            destinationBusStopDescription = busRouteNodeList.last().busStopDescription
        )
    }

    companion object {
        fun String.ltaApiTimeStrToLocalHourMinute(): LocalHourMinute =
            LocalHourMinute(
                hour = substring(0..1)
                    .let {
                        if (it == "24") {
                            "00"
                        } else {
                            it
                        }
                    }
                    .toIntOrNull()
                    ?: 0,
                minute = substring(2..3)
                    .let {
                        if (it == "0`") {
                            "00"
                        } else {
                            it
                        }
                    }
                    .toIntOrNull()
                    ?: 0
            )
    }
}