package io.github.amanshuraikwar.nsapi

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.github.amanshuraikwar.nsapi.db.NsApiDb
import io.github.amanshuraikwar.nsapi.db.NsTrainStationEntity
import io.github.amanshuraikwar.nsapi.model.TrainJourneyDetailsStopArrivalDto
import io.github.amanshuraikwar.nsapi.model.TrainJourneyDetailsStopDepartureDto
import io.github.amanshuraikwar.nsapi.model.TrainJourneyDetailsStopDto
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.MapUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.toSearchDescriptionHint
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainCrowdStatus
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDeparture
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDepartureStatus
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDetails
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainFacility
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRollingStock
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNode
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNodeTiming
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainRouteNodeType
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainStop
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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
                    val arrivalDto = arrivalsMap[departureDto.product.number]

                    departures.add(
                        TrainDeparture(
                            trainCode = departureDto.product.number,
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
                            plannedArrivalInstant =
                            arrivalDto?.plannedDateTime?.toAmsterdamInstant(),
                            actualArrivalInstant =
                            arrivalDto?.actualDateTime?.toAmsterdamInstant(),
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

    override suspend fun supportsTrain(trainCode: String): Boolean {
        // TODO-amanshuraikwar (18 Sep 2022 02:11:13 AM):
        //  only support NL trains
        return true
    }

    override suspend fun getTrainDetails(trainCode: String): TrainDetails {
        return withContext(dispatcherProvider.io) {
            val trainInfoDeferred = async {
                nsApi.getTrainInformation(
                    trainCodes = listOf(trainCode),
                    stationCodes = listOf()
                )
            }

            val trainJourneyDetailsInfoDefered = async {
                nsApi.getTrainJourneyDetails(trainCode = trainCode)
            }

            val trainInfo = trainInfoDeferred.await()[0]
            val trainJourneyDetails = trainJourneyDetailsInfoDefered.await().payload

            // TODO: assuming train always as departure info
            //       get it from somewhere safer
            val trainCategoryName =
                trainJourneyDetails.stops[0].departures[0].product.longCategoryName

            val accumulatedFacilities = trainInfo
                .materieeldelen
                .fold(mutableSetOf<TrainFacility>()) { currentSet, trainInfoMaterialDto ->
                    for (facility in trainInfoMaterialDto.faciliteiten) {
                        currentSet.add(facility.toTrainFacility())
                    }
                    currentSet
                }
                .toList()

            val rollingStock = trainInfo.materieeldelen.map { trainInfoMaterialDto ->
                TrainRollingStock(
                    type = trainInfoMaterialDto.type,
                    facilities = trainInfoMaterialDto.faciliteiten.map { it.toTrainFacility() },
                    imageUrl = trainInfoMaterialDto.afbeelding,
                    width = trainInfoMaterialDto.breedte,
                    height = trainInfoMaterialDto.hoogte,
                )
            }

            val route = trainJourneyDetails.stops.mapIndexed { index, stopDto ->
                stopDto.toTrainRouteNode(index)
            }

            TrainDetails(
                trainCode = trainInfo.ritnummer.toString(),
                trainCategoryName = trainCategoryName,
                sourceTrainStopName = trainJourneyDetails.stops.first().stop.name,
                destinationTrainStopName = trainJourneyDetails.stops.last().stop.name,
                facilities = accumulatedFacilities,
                rollingStock = rollingStock,
                length = trainInfo.lengte,
                lengthInMeters = trainInfo.lengteInMeters,
                route = route
            )
        }
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
                        evaCode = stationDto.EVACode,
                        uicCode = stationDto.UICCode,
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

        private fun String.toTrainFacility(): TrainFacility {
            return when (this) {
                "TOILET" -> TrainFacility.TOILET
                "STROOM" -> TrainFacility.POWER_SOCKETS
                "WIFI" -> TrainFacility.WIFI
                "STILTE" -> TrainFacility.QUIET_TRAIN
                "FIETS" -> TrainFacility.BICYCLE
                "TOEGANKELIJK" -> TrainFacility.WHEELCHAIR_ACCESSIBLE
                else -> {
                    throw IllegalArgumentException(
                        "Unsupported facility $this"
                    )
                }
            }
        }

        private fun List<TrainJourneyDetailsStopDepartureDto>.toTrainRouteNodeDepartureTiming(): TrainRouteNodeTiming {
            return getOrNull(0)
                ?.let { departureDto ->
                    TrainRouteNodeTiming.Available(
                        plannedTime =
                        departureDto.plannedTime.toAmsterdamInstant().formatArrivalInstant(),
                        actualTime =
                        departureDto.actualTime?.toAmsterdamInstant()?.formatArrivalInstant(),
                        delayedByMinutes =
                        departureDto
                            .actualTime
                            ?.toAmsterdamInstant()
                            ?.minus(
                                departureDto
                                    .plannedTime
                                    .toAmsterdamInstant()
                            )
                            ?.inWholeMinutes
                            ?.toInt()
                            ?: 0,
                        plannedTrack = departureDto.plannedTrack,
                        actualTrack = departureDto.actualTrack,
                        cancelled = departureDto.cancelled
                    )
                }
                ?: TrainRouteNodeTiming.NoData
        }

        private fun List<TrainJourneyDetailsStopArrivalDto>.toTrainRouteNodeArrivalTiming(): TrainRouteNodeTiming {
            return getOrNull(0)
                ?.let { arrivalDto ->
                    TrainRouteNodeTiming.Available(
                        plannedTime =
                        arrivalDto.plannedTime.toAmsterdamInstant().formatArrivalInstant(),
                        actualTime =
                        arrivalDto.actualTime?.toAmsterdamInstant()?.formatArrivalInstant(),
                        delayedByMinutes =
                        arrivalDto
                            .actualTime
                            ?.toAmsterdamInstant()
                            ?.minus(
                                arrivalDto
                                    .plannedTime
                                    .toAmsterdamInstant()
                            )
                            ?.inWholeMinutes
                            ?.toInt()
                            ?: 0,
                        plannedTrack = arrivalDto.plannedTrack,
                        actualTrack = arrivalDto.actualTrack,
                        cancelled = arrivalDto.cancelled
                    )
                }
                ?: TrainRouteNodeTiming.NoData
        }

        private fun TrainJourneyDetailsStopDto.toTrainRouteNode(index: Int): TrainRouteNode {
            return TrainRouteNode(
                index = index,
                // assuming id is in the format CODE_0
                trainStopCode = id.split("_")[0],
                // only support NL stops
                supportedStop = stop.countryCode == "NL",
                trainStopName = stop.name,
                countryCode = stop.countryCode,
                type = when (status) {
                    "ORIGIN" -> {
                        TrainRouteNodeType.Origin(
                            departureTiming = departures.toTrainRouteNodeDepartureTiming()
                        )
                    }
                    "STOP" -> {
                        TrainRouteNodeType.Stop(
                            arrivalTiming = arrivals.toTrainRouteNodeArrivalTiming(),
                            departureTiming = departures.toTrainRouteNodeDepartureTiming(),
                        )
                    }
                    "PASSING" -> {
                        TrainRouteNodeType.Passing
                    }
                    "DESTINATION" -> {
                        TrainRouteNodeType.Destination(
                            arrivalTiming = arrivals.toTrainRouteNodeArrivalTiming(),
                        )
                    }
                    else -> {
                        throw IllegalArgumentException(
                            "Unsupported stop status $status"
                        )
                    }
                },
                crowdStatus =
                departures
                    .getOrNull(0)
                    ?.crowdForecast
                    ?.let { TrainCrowdStatus.valueOf(it) }
                    ?: arrivals
                        .getOrNull(0)
                        ?.crowdForecast
                        ?.let { TrainCrowdStatus.valueOf(it) }
                    ?: TrainCrowdStatus.UNKNOWN
            )
        }

        private fun Instant.formatArrivalInstant(): String {
            val datetimeInSystemZone = toLocalDateTime(TimeZone.currentSystemDefault())
            val hour = when (datetimeInSystemZone.hour) {
                0, 12 -> "12"
                in 1..9 -> "0${datetimeInSystemZone.hour}"
                in 13..21 -> "0${datetimeInSystemZone.hour % 12}"
                else -> "${datetimeInSystemZone.hour % 12}"
            }
            val a = when (datetimeInSystemZone.hour) {
                in 0..11 -> "am"
                in 12..23 -> "pm"
                else -> throw IllegalArgumentException("Invalid hour $hour")
            }
            val minutes = when (datetimeInSystemZone.minute) {
                in 0..9 -> "0${datetimeInSystemZone.minute}"
                else -> "${datetimeInSystemZone.minute}"
            }

            return "$hour:$minutes $a"
        }
    }
}