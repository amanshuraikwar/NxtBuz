package io.github.amanshuraikwar.nxtbuz.data

import io.github.amanshuraikwar.ltaapi.LtaApi
import io.github.amanshuraikwar.ltaapi.di.BusApiProvides
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.common.model.room.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.common.model.room.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.data.busstop.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SetupUnitTest {

    private val busStopDao = object : BusStopDao {

        val entities = mutableListOf<BusStopEntity>()

        override suspend fun insertAll(entities: List<BusStopEntity>) {
            this.entities.addAll(entities)
        }

        override suspend fun deleteAll() {
            entities.clear()
        }

        override suspend fun findAll(): List<BusStopEntity> {
            return entities
        }

        override suspend fun findByCode(code: String): List<BusStopEntity> {
            TODO("Not yet implemented")
        }

        override suspend fun findCloseLimit(
            latitude: Double,
            longitude: Double,
            limit: Int
        ): List<BusStopEntity> {
            TODO("Not yet implemented")
        }

        override suspend fun findCloseDistance(
            latitude: Double,
            longitude: Double,
            distance: Double
        ): List<BusStopEntity> {
            TODO("Not yet implemented")
        }

        override suspend fun searchLikeDescription(
            description: String,
            limit: Int
        ): List<BusStopEntity> {
            TODO("Not yet implemented")
        }
    }
    private val operatingBusDao = object : OperatingBusDao {
        override suspend fun insertAll(entities: List<OperatingBusEntity>) {
            TODO("Not yet implemented")
        }

        override suspend fun delete(entity: OperatingBusEntity) {
            TODO("Not yet implemented")
        }

        override suspend fun deleteAll() {
            TODO("Not yet implemented")
        }

        override suspend fun findAll(): List<OperatingBusEntity> {
            TODO("Not yet implemented")
        }

        override suspend fun findByBusStopCode(busStopCode: String): List<OperatingBusEntity> {
            TODO("Not yet implemented")
        }

        override suspend fun findByBusStopCodeAndBusServiceNumber(
            busStopCode: String,
            busServiceNumber: String
        ): List<OperatingBusEntity> {
            TODO("Not yet implemented")
        }

    }
    private val ltaApi: LtaApi by lazy { BusApiProvides().a() }
    private val preferenceStorage = object : PreferenceStorage {
        override var onboardingCompleted: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        override var busStopsQueryLimit: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override var defaultLocation: Pair<Double, Double>
            get() = TODO("Not yet implemented")
            set(value) {}
        override var maxDistanceOfClosestBusStop: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override var showErrorStarredBusArrivals: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        override var alertStarredBusArrivals: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        override var alertStarredBusArrivalsMinutes: Int
            get() = TODO("Not yet implemented")
            set(value) {}
        override var alertStarredBusArrivalsFrequency: AlertFrequency
            get() = TODO("Not yet implemented")
            set(value) {}

    }
    private val dispatcherProvider = CoroutinesDispatcherProvider()

    @Test
    fun setupBusStopsTest() {
        val busStopRepo = BusStopRepository(
            busStopDao,
            operatingBusDao,
            ltaApi,
            preferenceStorage,
            dispatcherProvider
        )
        runBlocking {
            busStopRepo.setup().collect {
                println("Output = $it")
            }
            print("${busStopDao.entities.size} bus stops")
            assertEquals(5035, busStopDao.entities.size)
        }
    }
}