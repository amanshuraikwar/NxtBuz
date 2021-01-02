package io.github.amanshuraikwar.nxtbuz.data.busroute

import android.util.Log
import io.github.amanshuraikwar.ltaapi.LtaApi
import io.github.amanshuraikwar.ltaapi.model.BusRouteItemDto
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.BusRoute
import io.github.amanshuraikwar.nxtbuz.common.model.BusRouteNode
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusRouteDao
import io.github.amanshuraikwar.nxtbuz.common.model.room.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import io.github.amanshuraikwar.nxtbuz.common.model.room.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.room.dao.StarredBusStopsDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.OffsetTime
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusRouteRepository @Inject constructor(
    private val busRouteDao: BusRouteDao,
    private val operatingBusDao: OperatingBusDao,
    private val busStopDao: BusStopDao,
    private val starredBusStopsDao: StarredBusStopsDao,
    private val busApi: LtaApi,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    fun setup(): Flow<Double> = flow {

        emit(0.0)

        busRouteDao.deleteAll()
        operatingBusDao.deleteAll()

        val startTimeMillis = System.currentTimeMillis()

        val (busStopCodeMap, busServiceNumberMap) = getBusRouteApiOutput(this)

        saveOperatingBusData(busStopCodeMap)

        emit(0.9)

        saveBusRouteData(busServiceNumberMap)

        Log.i(
            TAG,
            "setup: Setup took ${(System.currentTimeMillis() - startTimeMillis) / 1000} seconds"
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

        val remoteBusRouteDeferredList = mutableListOf<Deferred<Unit>>()

        var skip = 0
        var shouldStop = false

        // run infinitely
        // until the api starts responding with empty list
        // that's when we know we have queried all possible items
        while (!shouldStop) {

            // thread local to pass skip variable safely
            val threadLocal = ThreadLocal<Int>()

            remoteBusRouteDeferredList.add(
                async(dispatcherProvider.pool8 + threadLocal.asContextElement(skip)) {

                    val threadLocalSkip = threadLocal.get() ?: throw IOException(
                        "Something went wrong while getting skip param."
                    )

                    val busRouteItemList = busApi.getBusRoutes(threadLocalSkip).busRouteList

                    // if bus route list fetched is empty
                    // set flag to stop the loop
                    if (busRouteItemList.isEmpty()) {
                        shouldStop = true
                    }

                    busRouteItemList.forEach { busRouteItemDto ->

                        synchronized(busStopCodeBusRouteItemListMap) {
                            busStopCodeBusRouteItemListMap[busRouteItemDto.busStopCode]
                                ?.add(busRouteItemDto)
                                ?: run {
                                    busStopCodeBusRouteItemListMap[busRouteItemDto.busStopCode] =
                                        mutableSetOf(busRouteItemDto)
                                }
                        }

                        synchronized(busServiceNumberBusRouteItemListMap) {
                            busServiceNumberBusRouteItemListMap[busRouteItemDto.serviceNumber]
                                ?.add(busRouteItemDto)
                                ?: run {
                                    busServiceNumberBusRouteItemListMap[busRouteItemDto.serviceNumber] =
                                        mutableSetOf(busRouteItemDto)
                                }
                        }
                    }
                }
            )

            // we wait for all the coroutine jobs to finish if EMPTY_ASYNC_LIMIT jobs are spawned
            // to limit the number of possible empty async coroutines
            // prevent the infinite loop to spawn a huge number of empty response remote api calls
            // only to wait for them to finish
            // also to prevent over-loading the thread pool
            if (remoteBusRouteDeferredList.size == EMPTY_ASYNC_LIMIT) {
                remoteBusRouteDeferredList.awaitAll()
                remoteBusRouteDeferredList.clear()
            }

            skip += 500

            flowCollector.emit(
                0.8.coerceAtMost((skip.toDouble() / 32000.0) * 0.8)
            )
        }

        // wait for any remaining coroutine jobs to complete
        remoteBusRouteDeferredList.awaitAll()

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
                    operatingBusDao.insertAll(
                        busRouteItem.map {
                            OperatingBusEntity(
                                busStopCode = busStopCode,
                                busServiceNumber = it.serviceNumber,
                                wdFirstBus = if (it.wdFirstBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.wdFirstBus.toTime())
                                },
                                wdLastBus = if (it.wdLastBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.wdLastBus.toTime())
                                },
                                satFirstBus = if (it.satFirstBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.satFirstBus.toTime())
                                },
                                satLastBus = if (it.satLastBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.satLastBus.toTime())
                                },
                                sunFirstBus = if (it.sunFirstBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.sunFirstBus.toTime())
                                },
                                sunLastBus = if (it.sunLastBus == "-") {
                                    null
                                } else {
                                    OffsetTime.parse(it.sunLastBus.toTime())
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
                    busRouteDao.insertAll(
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

    suspend fun getBusRoute(
        busServiceNumber: String,
        direction: Int? = null,
        busStopCode: String? = null
    ): BusRoute = withContext(dispatcherProvider.io) {

        val busRouteEntityList = busRouteDao.findByBusServiceNumber(busServiceNumber)

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
                        val busStopEntity = busStopDao.findByCode(busRouteEntity.busStopCode).let {
                            if (it.isEmpty()) throw Exception("No bus stop found for bus stop code ${busRouteEntity.busStopCode}")
                            it[0]
                        }
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
                starredBusStopsDao
                    .findByBusStopCode(busStopCode)
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
        const val EMPTY_ASYNC_LIMIT = 16
        private const val TAG = "BusRouteRepository"
        fun String.toTime() =
            substring(0..1).let { if (it == "24") "00" else it } +
                    ":" +
                    substring(2..3).let { if (it == "0`") "00" else it } +
                    ":00+08:00"
    }
}