package io.github.amanshuraikwar.nxtbuz.searchdata

import com.squareup.sqldelight.db.SqlDriver
import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.SearchResult
import io.github.amanshuraikwar.nxtbuz.localdatasource.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.OperatingBusEntity
import io.github.amanshuraikwar.nxtbuz.sqldelightdb.SqlDelightLocalDataSource
import io.github.amanshuraikwar.testutil.runTest
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// TODO-amanshuraikwar (31 Dec 2021 08:57:14 PM): test for bus services as well
class SearchRepositoryTest {
    private val fakeCoroutinesDispatcherProvider = CoroutinesDispatcherProvider(
        main = Dispatchers.Default,
        computation = Dispatchers.Default,
        io = Dispatchers.Default,
        pool8 = Dispatchers.Default,
        map = Dispatchers.Default,
        arrivalService = Dispatchers.Default,
        location = Dispatchers.Default,
    )

    private val driver = getSqlDriver()

    private val localDataSource = SqlDelightLocalDataSource.createInstance(
        driver = driver ?: throw Exception("SQL Driver is null"),
        ioDispatcher = fakeCoroutinesDispatcherProvider.io
    )

    private val repo = SearchRepositoryImpl(
        dispatcherProvider = fakeCoroutinesDispatcherProvider,
        localDataSource = localDataSource
    ).apply {
        runTest {
            localDataSource.insertBusStops(
                listOf(
                    BusStopEntity(
                        code = "11401",
                        roadName = "Holland Ave",
                        description = "Holland V Stn/Blk12",
                        latitude = 0.0,
                        longitude = 0.0
                    ),
                    BusStopEntity(
                        code = "11381",
                        roadName = "Holland Dr",
                        description = "Blk 10A",
                        latitude = 0.0,
                        longitude = 0.0
                    )
                )
            )

            localDataSource.insertOperatingBuses(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "11401",
                        busServiceNumber = "106",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    ),
                    OperatingBusEntity(
                        busStopCode = "11401",
                        busServiceNumber = "61",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    ),
                    OperatingBusEntity(
                        busStopCode = "11381",
                        busServiceNumber = "145",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    ),
                    OperatingBusEntity(
                        busStopCode = "11381",
                        busServiceNumber = "185",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                )
            )
        }
    }

    @Test
    fun `bus stop description starts with search query`() {
        runTest {
            assertEquals(
                expected = SearchResult(
                    busServiceList = emptyList(),
                    busStopList = listOf(
                        BusStop(
                            code = "11401",
                            roadName = "Holland Ave",
                            description = "Holland V Stn/Blk12",
                            latitude = 0.0,
                            longitude = 0.0,
                            operatingBusList = listOf(
                                Bus(serviceNumber = "106"),
                                Bus(serviceNumber = "61"),
                            )
                        )
                    )
                ),
                actual = repo.search("Hol", 2)
            )
        }
    }

    @Test
    fun `bus stop description starts with search query, search query has spaces`() {
        runTest {
            assertEquals(
                expected = SearchResult(
                    busServiceList = emptyList(),
                    busStopList = listOf(
                        BusStop(
                            code = "11401",
                            roadName = "Holland Ave",
                            description = "Holland V Stn/Blk12",
                            latitude = 0.0,
                            longitude = 0.0,
                            operatingBusList = listOf(
                                Bus(serviceNumber = "106"),
                                Bus(serviceNumber = "61"),
                            )
                        )
                    )
                ),
                actual = repo.search("  H o  l ", 2)
            )
        }
    }

    @Test
    fun `bus stop description starts with search query, search query has spaces and differnt case`() {
        runTest {
            assertEquals(
                expected = SearchResult(
                    busServiceList = emptyList(),
                    busStopList = listOf(
                        BusStop(
                            code = "11401",
                            roadName = "Holland Ave",
                            description = "Holland V Stn/Blk12",
                            latitude = 0.0,
                            longitude = 0.0,
                            operatingBusList = listOf(
                                Bus(serviceNumber = "106"),
                                Bus(serviceNumber = "61"),
                            )
                        )
                    )
                ),
                actual = repo.search("  h O  L ", 2)
            )
        }
    }

    @Test
    fun `bus stop description is in middle of the search query`() {
        runTest {
            assertEquals(
                expected = SearchResult(
                    busServiceList = emptyList(),
                    busStopList = listOf(
                        BusStop(
                            code = "11401",
                            roadName = "Holland Ave",
                            description = "Holland V Stn/Blk12",
                            latitude = 0.0,
                            longitude = 0.0,
                            operatingBusList = listOf(
                                Bus(serviceNumber = "106"),
                                Bus(serviceNumber = "61"),
                            )
                        )
                    )
                ),
                actual = repo.search("and V", 2)
            )
        }
    }

    @Test
    fun `bus stop description is in middle of the search query, has spaces and different case`() {
        runTest {
            assertEquals(
                expected = SearchResult(
                    busServiceList = emptyList(),
                    busStopList = listOf(
                        BusStop(
                            code = "11401",
                            roadName = "Holland Ave",
                            description = "Holland V Stn/Blk12",
                            latitude = 0.0,
                            longitude = 0.0,
                            operatingBusList = listOf(
                                Bus(serviceNumber = "106"),
                                Bus(serviceNumber = "61"),
                            )
                        )
                    )
                ),
                actual = repo.search("AN  dv", 2)
            )
        }
    }

    @Test
    fun `bus stop description ends with search query, has spaces and different case, includes numbers and is missing special character`() {
        runTest {
            assertEquals(
                expected = SearchResult(
                    busServiceList = emptyList(),
                    busStopList = listOf(
                        BusStop(
                            code = "11401",
                            roadName = "Holland Ave",
                            description = "Holland V Stn/Blk12",
                            latitude = 0.0,
                            longitude = 0.0,
                            operatingBusList = listOf(
                                Bus(serviceNumber = "106"),
                                Bus(serviceNumber = "61"),
                            )
                        )
                    )
                ),
                actual = repo.search("tn  b L K  12", 2)
            )
        }
    }
}

expect fun getSqlDriver(): SqlDriver?