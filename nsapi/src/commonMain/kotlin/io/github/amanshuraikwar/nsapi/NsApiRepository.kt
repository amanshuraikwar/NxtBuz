package io.github.amanshuraikwar.nsapi

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.github.amanshuraikwar.nsapi.db.NsApiDb
import io.github.amanshuraikwar.nsapi.db.NsTrainStationEntity
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.MapUtil
import io.github.amanshuraikwar.nxtbuz.commonkmm.TrainStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.toSearchDescriptionHint
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository
import kotlinx.coroutines.withContext

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
                    TrainStop(
                        type = nsTrainStationEntity.stationType,
                        code = nsTrainStationEntity.code,
                        hasFacilities = nsTrainStationEntity.hasFacilities,
                        hasDepartureTimes = nsTrainStationEntity.hasDepartureTimes,
                        hasTravelAssistance = nsTrainStationEntity.hasTravelAssistance,
                        name = nsTrainStationEntity.nameLong,
                        lat = nsTrainStationEntity.latitude,
                        lng = nsTrainStationEntity.longitude,
                        starred = nsTrainStationEntity.starred
                    )
                }
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
    }
}