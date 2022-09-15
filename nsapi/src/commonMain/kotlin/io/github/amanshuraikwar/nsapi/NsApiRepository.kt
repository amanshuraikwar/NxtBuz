package io.github.amanshuraikwar.nsapi

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.github.amanshuraikwar.nsapi.db.NsApiDb
import io.github.amanshuraikwar.nsapi.db.NsTrainStationEntity
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.MapUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.toSearchDescriptionHint
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDeparture
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDepartureStatus
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

internal class NsApiRepository(
    private val settingsFactory: () -> Settings,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val nsApi: NsApi,
    private val nsApiDb: NsApiDb
) : TrainStopRepository {
    private val settings by lazy {
        settingsFactory()
    }

    override suspend fun supportsLocation(lat: Double, lng: Double): Boolean {
        // TODO-amanshuraikwar (11 Sep 2022 12:54:59 PM):
        //  update to check if location is inside or near The Netherlands
        return true
    }

    override suspend fun getCloseTrainStops(
        lat: Double,
        lng: Double,
        maxStops: Int,
        maxDistanceMetres: Int?
    ): List<TrainStop> {
        return withContext(dispatcherProvider.io) {
            val cachedLocally =
                settings.getBoolean(PREF_TRAIN_STOPS_CACHED_LOCALLY, false)

            if (!cachedLocally) {
                fetchAndCacheLocally()
            }

            nsApiDb
                .nsTrainStationEntityQueries
                .findClose(
                    latitude = lat,
                    longitude = lng,
                    limit = maxStops.toLong()
                )
                .executeAsList()
                .let {
                    if (maxDistanceMetres != null) {
                        it.filter { nsTrainStationEntity ->
                            MapUtil.measureDistanceMetres(
                                lat1 = lat,
                                lng1 = lng,
                                lat2 = nsTrainStationEntity.latitude,
                                lng2 = nsTrainStationEntity.longitude
                            ) <= maxDistanceMetres
                        }
                    } else {
                        it
                    }
                }
                .distinctBy { it.code }
                .map { nsTrainStationEntity ->
                    nsTrainStationEntity.toTrainStop()
                }
        }
    }

    override suspend fun containsStop(code: String): Boolean {
        return code.startsWith(TRAIN_STOP_CODE_PREFIX)
    }

    override suspend fun getTrainDepartures(trainStopCode: String): List<TrainDeparture> {
        val trainStationCode = trainStopCode.drop(TRAIN_STOP_CODE_PREFIX.length)
        return withContext(dispatcherProvider.io) {
            val departures = mutableListOf<TrainDeparture>()

            val arrivalDeferred = async {
                nsApi.getTrainArrivals(stationCode = trainStationCode)
            }
            val departuresDeferred = async {
                nsApi.getTrainDepartures(stationCode = trainStationCode)
            }

            val arrivalsMap = arrivalDeferred.await()
                .payload
                .arrivals
                .groupBy { arrivalDto ->
                    arrivalDto.product.number
                }
                .mapValues {
                    it.value[0]
                }

            departuresDeferred.await()
                .payload
                .departures
                .forEach { departureDto ->
                    val arrivalDto = arrivalsMap[departureDto.product.number] ?: return@forEach

                    departures.add(
                        TrainDeparture(
                            id = departureDto.product.number,
                            destinationTrainStopName = departureDto.direction,
                            track = departureDto.plannedTrack,
                            trainCategoryName = departureDto.product.shortCategoryName,
                            departureStatus = when {
                                departureDto.cancelled -> {
                                    TrainDepartureStatus.CANCELLED
                                }
                                departureDto.departureStatus == "ON_STATION" -> {
                                    TrainDepartureStatus.ON_STATION
                                }
                                departureDto.departureStatus == "INCOMING" -> {
                                    TrainDepartureStatus.INCOMING
                                }
                                else -> {
                                    TrainDepartureStatus.UNKNOWN
                                }
                            },
                            plannedArrivalInstant = arrivalDto.plannedDateTime.toAmsterdamInstant(),
                            actualArrivalInstant = arrivalDto.actualDateTime.toAmsterdamInstant(),
                            plannedDepartureInstant = departureDto.plannedDateTime.toAmsterdamInstant(),
                            actualDepartureInstant = departureDto.actualDateTime.toAmsterdamInstant(),
                            delayedByMinutes =
                            departureDto
                                .actualDateTime
                                .toAmsterdamInstant().minus(
                                    departureDto
                                        .plannedDateTime
                                        .toAmsterdamInstant()
                                )
                                .inWholeMinutes.toInt()
                        )
                    )
                }

            departures
        }
    }

    override suspend fun getTrainStop(code: String): TrainStop? {
        return withContext(dispatcherProvider.io) {
            nsApiDb
                .nsTrainStationEntityQueries
                .findByCode(code = code.drop(TRAIN_STOP_CODE_PREFIX.length))
                .executeAsList()
                .getOrNull(0)
                ?.toTrainStop()
        }
    }

    private fun String.toAmsterdamInstant(): Instant {
        val (localDateTimeString, _) = split("+")
        return LocalDateTime.parse(localDateTimeString)
            .toInstant(TimeZone.of("Europe/Amsterdam"))
    }

    private fun NsTrainStationEntity.toTrainStop(): TrainStop {
        return TrainStop(
            type = stationType,
            code = TRAIN_STOP_CODE_PREFIX + code,
            codeToDisplay = code,
            hasFacilities = hasFacilities,
            hasDepartureTimes = hasDepartureTimes,
            hasTravelAssistance = hasTravelAssistance,
            name = nameLong,
            lat = latitude,
            lng = longitude,
            starred = starred
        )
    }

    private suspend fun fetchAndCacheLocally() {
        withContext(dispatcherProvider.io) {
            settings[PREF_TRAIN_STOPS_CACHED_LOCALLY] = false

            val entities = nsApi.getStations()
                .payload
                .map { stationDto ->
                    NsTrainStationEntity(
                        stationType = stationDto.stationType,
                        code = stationDto.code,
                        hasFacilities = stationDto.heeftFaciliteiten,
                        hasDepartureTimes = stationDto.heeftVertrektijden,
                        hasTravelAssistance = stationDto.heeftReisassistentie,
                        nameShort = stationDto.namen.kort,
                        nameMedium = stationDto.namen.middel,
                        nameLong = stationDto.namen.lang,
                        land = stationDto.land,
                        latitude = stationDto.lat,
                        longitude = stationDto.lng,
                        entryDate = stationDto.ingangsDatum,
                        nearbyMeLocationType = stationDto.nearbyMeLocationId.type,
                        nearbyMeLocationValue = stationDto.nearbyMeLocationId.value,
                        starred = false,
                        descriptionSearchKey =
                        (stationDto.namen.kort + stationDto.namen.middel + stationDto.namen.lang)
                            .toSearchDescriptionHint()
                    )
                }

            nsApiDb.nsTrainStationEntityQueries.transaction {
                entities
                    .distinctBy { it.code }
                    .forEach { entity ->
                        nsApiDb.nsTrainStationEntityQueries.insertOrReplace(entity)
                    }
            }

            settings[PREF_TRAIN_STOPS_CACHED_LOCALLY] = true
        }
    }

    companion object {
        private const val PREF_TRAIN_STOPS_CACHED_LOCALLY = "TRAIN_STOPS_CACHED_LOCALLY"
        private const val TRAIN_STOP_CODE_PREFIX = "NS-API-TRAIN-"
    }
}