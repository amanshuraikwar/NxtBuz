package io.github.amanshuraikwar.nxtbuz.busstopdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.Bus
import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBus
import io.github.amanshuraikwar.nxtbuz.commonkmm.goinghome.DirectBusesResult
import io.github.amanshuraikwar.nxtbuz.localdatasource.BusStopEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.DirectBusEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.OperatingBusEntity
import io.github.amanshuraikwar.testutil.FakeCoroutinesDispatcherProvider
import io.github.amanshuraikwar.testutil.FakeLocalDataSource
import io.github.amanshuraikwar.testutil.FakePreferenceStorage
import io.github.amanshuraikwar.testutil.FakeRemoteDataSource
import io.github.amanshuraikwar.testutil.runTest
import kotlinx.coroutines.flow.collect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BusStopRepositoryTest {

    @Test
    fun `setup populates the correct bus stop data in local storage, local storage is initially empty`() {
        val localDataSource = FakeLocalDataSource()

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource { url ->
                if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 0
                ) {
                    """
                    {
                        "odata.metadata": "this is fake data",
                        "value": [
                            {
                              "BusStopCode": "01012",
                              "RoadName": "Victoria St",
                              "Description": "Hotel Grand Pacific",
                              "Latitude": 1.29684825487647,
                              "Longitude": 103.85253591654006
                            },
                            {
                              "BusStopCode": "01013",
                              "RoadName": "Victoria St",
                              "Description": "St. Joseph's Ch",
                              "Latitude": 1.29770970610083,
                              "Longitude": 103.8532247463225
                            }
                        ]
                    }
                """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 500
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                  "BusStopCode": "01019",
                                  "RoadName": "Victoria St",
                                  "Description": "Bras Basah Cplx",
                                  "Latitude": 1.29698951191332,
                                  "Longitude": 103.85302201172507
                                },
                                {
                                  "BusStopCode": "01029",
                                  "RoadName": "Nth Bridge Rd",
                                  "Description": "Opp Natl Lib",
                                  "Latitude": 1.2966729849642,
                                  "Longitude": 103.85441422464267
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 1000
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                  "BusStopCode": "01039",
                                  "RoadName": "Nth Bridge Rd",
                                  "Description": "Bugis Cube",
                                  "Latitude": 1.29820784139683,
                                  "Longitude": 103.85549139837407
                                },
                                {
                                  "BusStopCode": "01059",
                                  "RoadName": "Victoria St",
                                  "Description": "Bugis Stn Exit B",
                                  "Latitude": 1.30075679526626,
                                  "Longitude": 103.85611040457583
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 1500
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": []
                        }
                    """.trimIndent()
                } else {
                    ""
                }
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setup().collect {
                println("bus stops setup progress: $it")
            }

            assertEquals(
                listOf(
                    BusStopEntity(
                        code = "01012",
                        roadName = "Victoria St",
                        description = "Hotel Grand Pacific",
                        latitude = 1.29684825487647,
                        longitude = 103.85253591654006
                    ),
                    BusStopEntity(
                        code = "01013",
                        roadName = "Victoria St",
                        description = "St. Joseph's Ch",
                        latitude = 1.29770970610083,
                        longitude = 103.8532247463225
                    ),
                    BusStopEntity(
                        code = "01019",
                        roadName = "Victoria St",
                        description = "Bras Basah Cplx",
                        latitude = 1.29698951191332,
                        longitude = 103.85302201172507
                    ),
                    BusStopEntity(
                        code = "01029",
                        roadName = "Nth Bridge Rd",
                        description = "Opp Natl Lib",
                        latitude = 1.2966729849642,
                        longitude = 103.85441422464267
                    ),
                    BusStopEntity(
                        code = "01039",
                        roadName = "Nth Bridge Rd",
                        description = "Bugis Cube",
                        latitude = 1.29820784139683,
                        longitude = 103.85549139837407
                    ),
                    BusStopEntity(
                        code = "01059",
                        roadName = "Victoria St",
                        description = "Bugis Stn Exit B",
                        latitude = 1.30075679526626,
                        longitude = 103.85611040457583
                    ),
                ),
                localDataSource.getAllBusStops()
            )
        }
    }

    @Test
    fun `setup populates the correct bus stop data in local storage, local storage is initially not empty`() {
        val localDataSource = FakeLocalDataSource()
        runTest {
            localDataSource.insertBusStops(
                listOf(
                    BusStopEntity(
                        code = "12345",
                        roadName = "poopoo st",
                        description = "Cuis Buge",
                        latitude = 1.0974,
                        longitude = 100.24
                    ),
                    BusStopEntity(
                        code = "23456",
                        roadName = "helohelo st",
                        description = "Cubis Nts Itxe C",
                        latitude = 1.572,
                        longitude = 101.24
                    ),
                )
            )
        }
        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource { url ->
                if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 0
                ) {
                    """
                    {
                        "odata.metadata": "this is fake data",
                        "value": [
                            {
                              "BusStopCode": "01012",
                              "RoadName": "Victoria St",
                              "Description": "Hotel Grand Pacific",
                              "Latitude": 1.29684825487647,
                              "Longitude": 103.85253591654006
                            },
                            {
                              "BusStopCode": "01013",
                              "RoadName": "Victoria St",
                              "Description": "St. Joseph's Ch",
                              "Latitude": 1.29770970610083,
                              "Longitude": 103.8532247463225
                            }
                        ]
                    }
                """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 500
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                  "BusStopCode": "01019",
                                  "RoadName": "Victoria St",
                                  "Description": "Bras Basah Cplx",
                                  "Latitude": 1.29698951191332,
                                  "Longitude": 103.85302201172507
                                },
                                {
                                  "BusStopCode": "01029",
                                  "RoadName": "Nth Bridge Rd",
                                  "Description": "Opp Natl Lib",
                                  "Latitude": 1.2966729849642,
                                  "Longitude": 103.85441422464267
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 1000
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                  "BusStopCode": "01039",
                                  "RoadName": "Nth Bridge Rd",
                                  "Description": "Bugis Cube",
                                  "Latitude": 1.29820784139683,
                                  "Longitude": 103.85549139837407
                                },
                                {
                                  "BusStopCode": "01059",
                                  "RoadName": "Victoria St",
                                  "Description": "Bugis Stn Exit B",
                                  "Latitude": 1.30075679526626,
                                  "Longitude": 103.85611040457583
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusStops")
                    && url.parameters["\$skip"]?.toInt() == 1500
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": []
                        }
                    """.trimIndent()
                } else {
                    ""
                }
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setup().collect {
                println("bus stops setup progress: $it")
            }

            assertEquals(
                listOf(
                    BusStopEntity(
                        code = "01012",
                        roadName = "Victoria St",
                        description = "Hotel Grand Pacific",
                        latitude = 1.29684825487647,
                        longitude = 103.85253591654006
                    ),
                    BusStopEntity(
                        code = "01013",
                        roadName = "Victoria St",
                        description = "St. Joseph's Ch",
                        latitude = 1.29770970610083,
                        longitude = 103.8532247463225
                    ),
                    BusStopEntity(
                        code = "01019",
                        roadName = "Victoria St",
                        description = "Bras Basah Cplx",
                        latitude = 1.29698951191332,
                        longitude = 103.85302201172507
                    ),
                    BusStopEntity(
                        code = "01029",
                        roadName = "Nth Bridge Rd",
                        description = "Opp Natl Lib",
                        latitude = 1.2966729849642,
                        longitude = 103.85441422464267
                    ),
                    BusStopEntity(
                        code = "01039",
                        roadName = "Nth Bridge Rd",
                        description = "Bugis Cube",
                        latitude = 1.29820784139683,
                        longitude = 103.85549139837407
                    ),
                    BusStopEntity(
                        code = "01059",
                        roadName = "Victoria St",
                        description = "Bugis Stn Exit B",
                        latitude = 1.30075679526626,
                        longitude = 103.85611040457583
                    ),
                ),
                localDataSource.getAllBusStops()
            )
        }
    }

    /*
        Code to generate coordinates:
        val x = 1.0
        val y = 1.0

        val z = sqrt(x.pow(2) + y.pow(2))
        z
        val list = mutableListOf<String>()
        for (i in 10 downTo 1) {
            println(sqrt(i.toFloat().pow(2) - 1.0))
        }
     */
    @Test
    fun `get close bus stops`() {
        val localDataSource = FakeLocalDataSource()

        runTest {
            localDataSource.insertBusStops(
                listOf(
                    BusStopEntity(
                        code = "5",
                        roadName = "5",
                        description = "5",
                        latitude = 1.0,
                        longitude = 5.916079783099616
                    ),
                    BusStopEntity(
                        code = "2",
                        roadName = "2",
                        description = "2",
                        latitude = 1.0,
                        longitude = 2.8284271247461903
                    ),
                    BusStopEntity(
                        code = "6",
                        roadName = "6",
                        description = "6",
                        latitude = 1.0,
                        longitude = 6.928203230275509
                    ),
                    BusStopEntity(
                        code = "9",
                        roadName = "9",
                        description = "9",
                        latitude = 1.0,
                        longitude = 9.9498743710662
                    ),
                    BusStopEntity(
                        code = "1",
                        roadName = "1",
                        description = "1",
                        latitude = 1.0,
                        longitude = 1.7320508075688772
                    ),
                    BusStopEntity(
                        code = "0",
                        roadName = "0",
                        description = "0",
                        latitude = 1.0,
                        longitude = 0.0
                    ),
                    BusStopEntity(
                        code = "4",
                        roadName = "4",
                        description = "4",
                        latitude = 1.0,
                        longitude = 4.898979485566356
                    ),
                    BusStopEntity(
                        code = "3",
                        roadName = "3",
                        description = "3",
                        latitude = 1.0,
                        longitude = 3.872983346207417
                    ),
                    BusStopEntity(
                        code = "8",
                        roadName = "8",
                        description = "8",
                        latitude = 1.0,
                        longitude = 8.94427190999916
                    ),
                    BusStopEntity(
                        code = "7",
                        roadName = "7",
                        description = "7",
                        latitude = 1.0,
                        longitude = 7.937253933193772
                    ),
                )
            )
        }

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertEquals(
                listOf(
                    BusStop(
                        code = "0",
                        roadName = "0",
                        description = "0",
                        latitude = 1.0,
                        longitude = 0.0,
                        operatingBusList = listOf(),
                    ),
                    BusStop(
                        code = "1",
                        roadName = "1",
                        description = "1",
                        latitude = 1.0,
                        longitude = 1.7320508075688772,
                        operatingBusList = listOf(),
                    ),
                    BusStop(
                        code = "2",
                        roadName = "2",
                        description = "2",
                        latitude = 1.0,
                        longitude = 2.8284271247461903,
                        operatingBusList = listOf(),
                    ),
                    BusStop(
                        code = "3",
                        roadName = "3",
                        description = "3",
                        latitude = 1.0,
                        longitude = 3.872983346207417,
                        operatingBusList = listOf(),
                    ),
                    BusStop(
                        code = "4",
                        roadName = "4",
                        description = "4",
                        latitude = 1.0,
                        longitude = 4.898979485566356,
                        operatingBusList = listOf(),
                    ),
                ),
                repo.getCloseBusStops(0.0, 0.0, 5)
            )
        }
    }

    /*
        import java.lang.Math.sqrt
        import kotlin.math.PI
        import kotlin.math.atan2
        import kotlin.math.cos
        import kotlin.math.pow
        import kotlin.math.sin

        data class Data(
            val x: Double,
            val y: Double,
            val z: Double,
            val dist: Double,
        )

        (10 downTo 1).map { i ->
            val x = 1
            val z = i
            val y = sqrt(i.toFloat().pow(2) - 1.0)
            Data(
                x = x.toDouble(),
                y = y,
                z = z.toDouble(),
                dist = dist(x.toDouble(), y)
            )
        }.forEach {
            println(it)
        }

        fun dist(x: Double, y: Double): Double {
            val lat1 = 0
            val lng1 = 0
            val lat2 = x
            val lng2 = y
            val r = 6378.137 // Radius of earth in KM
            val dLat = lat2 * PI / 180 - lat1 * PI / 180
            val dLon = lng2 * PI / 180 - lng1 * PI / 180
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1 * PI / 180) * cos(lat2 * PI / 180) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
            val d = r * c
            return d * 1000 // meters
        }
     */
    @Test
    fun `get close bus stops with max distance`() {
        val localDataSource = FakeLocalDataSource()

        runTest {
            localDataSource.insertBusStops(
                listOf(
                    BusStopEntity(
                        code = "5",
                        roadName = "5",
                        description = "5",
                        latitude = 1.0,
                        longitude = 5.916079783099616
                    ),
                    BusStopEntity(
                        code = "2",
                        roadName = "2",
                        description = "2",
                        latitude = 1.0,
                        longitude = 2.8284271247461903
                    ),
                    BusStopEntity(
                        code = "6",
                        roadName = "6",
                        description = "6",
                        latitude = 1.0,
                        longitude = 6.928203230275509
                    ),
                    BusStopEntity(
                        code = "9",
                        roadName = "9",
                        description = "9",
                        latitude = 1.0,
                        longitude = 9.9498743710662
                    ),
                    BusStopEntity(
                        code = "1",
                        roadName = "1",
                        description = "1",
                        latitude = 1.0,
                        longitude = 1.7320508075688772
                    ),
                    BusStopEntity(
                        code = "0",
                        roadName = "0",
                        description = "0",
                        latitude = 1.0,
                        longitude = 0.0
                    ),
                    BusStopEntity(
                        code = "4",
                        roadName = "4",
                        description = "4",
                        latitude = 1.0,
                        longitude = 4.898979485566356
                    ),
                    BusStopEntity(
                        code = "3",
                        roadName = "3",
                        description = "3",
                        latitude = 1.0,
                        longitude = 3.872983346207417
                    ),
                    BusStopEntity(
                        code = "8",
                        roadName = "8",
                        description = "8",
                        latitude = 1.0,
                        longitude = 8.94427190999916
                    ),
                    BusStopEntity(
                        code = "7",
                        roadName = "7",
                        description = "7",
                        latitude = 1.0,
                        longitude = 7.937253933193772
                    ),
                )
            )
        }

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertEquals(
                listOf(
                    BusStop(
                        code = "0",
                        roadName = "0",
                        description = "0",
                        latitude = 1.0,
                        longitude = 0.0,
                        operatingBusList = listOf(),
                    ),
                    BusStop(
                        code = "1",
                        roadName = "1",
                        description = "1",
                        latitude = 1.0,
                        longitude = 1.7320508075688772,
                        operatingBusList = listOf(),
                    ),
                ),
                repo.getCloseBusStops(0.0, 0.0, 5, metres = 333943)
            )
        }
    }

    @Test
    fun `get close bus stop query limit already stored in preference storage`() {
        val preferenceStorage = FakePreferenceStorage()
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource(),
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = preferenceStorage,
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        preferenceStorage.busStopsQueryLimit = 1042

        runTest {
            assertEquals(
                1042,
                repo.getBusStopQueryLimit()
            )
        }
    }

    @Test
    fun `get close bus stop query limit, after correct set values`() {
        val preferenceStorage = FakePreferenceStorage()
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource(),
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = preferenceStorage,
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setBusStopQueryLimit(1043)
            assertEquals(
                1043,
                repo.getBusStopQueryLimit()
            )
        }
    }

    @Test
    fun `get close bus stop query limit, after coerced set values, lower limit`() {
        val preferenceStorage = FakePreferenceStorage()
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource(),
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = preferenceStorage,
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setBusStopQueryLimit(-103)
            assertEquals(
                1,
                repo.getBusStopQueryLimit()
            )
        }
    }

    @Test
    fun `get max distance of closest bus stop already stored in preference storage`() {
        val preferenceStorage = FakePreferenceStorage()
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource(),
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = preferenceStorage,
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        preferenceStorage.maxDistanceOfClosestBusStop = 345

        runTest {
            assertEquals(
                345,
                repo.getMaxDistanceOfClosesBusStop()
            )
        }
    }

    @Test
    fun `get max distance of closest bus stop, after correct set values`() {
        val preferenceStorage = FakePreferenceStorage()
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource(),
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = preferenceStorage,
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setMaxDistanceOfClosesBusStop(176)
            assertEquals(
                176,
                repo.getMaxDistanceOfClosesBusStop()
            )
        }
    }

    @Test
    fun `get max distance of closest bus stop, after coerced set values, lower limit`() {
        val preferenceStorage = FakePreferenceStorage()
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource(),
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = preferenceStorage,
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setMaxDistanceOfClosesBusStop(-103)
            assertEquals(
                1,
                repo.getMaxDistanceOfClosesBusStop()
            )
        }
    }

    @Test
    fun `get bus stop and the bus stop & operating bus stops are in local storage`() {
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource().apply {
                runTest {
                    insertBusStops(
                        listOf(
                            BusStopEntity(
                                code = "01039",
                                roadName = "Nth Bridge Rd",
                                description = "Bugis Cube",
                                latitude = 1.29820784139683,
                                longitude = 103.85549139837407
                            ),
                            BusStopEntity(
                                code = "01059",
                                roadName = "Victoria St",
                                description = "Bugis Stn Exit B",
                                latitude = 1.30075679526626,
                                longitude = 103.85611040457583
                            ),
                        )
                    )

                    insertOperatingBuses(
                        listOf(
                            OperatingBusEntity(
                                busStopCode = "01059",
                                busServiceNumber = "961",
                                wdFirstBus = null,
                                wdLastBus = null,
                                satFirstBus = null,
                                satLastBus = null,
                                sunFirstBus = null,
                                sunLastBus = null,
                            ),
                            OperatingBusEntity(
                                busStopCode = "01059",
                                busServiceNumber = "106",
                                wdFirstBus = null,
                                wdLastBus = null,
                                satFirstBus = null,
                                satLastBus = null,
                                sunFirstBus = null,
                                sunLastBus = null,
                            ),
                            OperatingBusEntity(
                                busStopCode = "01049",
                                busServiceNumber = "77",
                                wdFirstBus = null,
                                wdLastBus = null,
                                satFirstBus = null,
                                satLastBus = null,
                                sunFirstBus = null,
                                sunLastBus = null,
                            )
                        )
                    )
                }
            },
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertEquals(
                BusStop(
                    code = "01059",
                    roadName = "Victoria St",
                    description = "Bugis Stn Exit B",
                    latitude = 1.30075679526626,
                    longitude = 103.85611040457583,
                    operatingBusList = listOf(
                        Bus(serviceNumber = "961"),
                        Bus(serviceNumber = "106"),
                    )
                ),
                repo.getBusStop(
                    busStopCode = "01059"
                )
            )
        }
    }

    @Test
    fun `get bus stop and the bus stop & no operating bus stops are in local storage`() {
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource().apply {
                runTest {
                    insertBusStops(
                        listOf(
                            BusStopEntity(
                                code = "01039",
                                roadName = "Nth Bridge Rd",
                                description = "Bugis Cube",
                                latitude = 1.29820784139683,
                                longitude = 103.85549139837407
                            ),
                            BusStopEntity(
                                code = "01059",
                                roadName = "Victoria St",
                                description = "Bugis Stn Exit B",
                                latitude = 1.30075679526626,
                                longitude = 103.85611040457583
                            ),
                        )
                    )
                }
            },
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertEquals(
                BusStop(
                    code = "01059",
                    roadName = "Victoria St",
                    description = "Bugis Stn Exit B",
                    latitude = 1.30075679526626,
                    longitude = 103.85611040457583,
                    operatingBusList = listOf()
                ),
                repo.getBusStop(
                    busStopCode = "01059"
                )
            )
        }
    }

    @Test
    fun `get bus stop and the bus stop is not in local storage`() {
        val repo = BusStopRepositoryImpl(
            localDataSource = FakeLocalDataSource().apply {
                runTest {
                    insertBusStops(
                        listOf(
                            BusStopEntity(
                                code = "01039",
                                roadName = "Nth Bridge Rd",
                                description = "Bugis Cube",
                                latitude = 1.29820784139683,
                                longitude = 103.85549139837407
                            ),
                            BusStopEntity(
                                code = "01059",
                                roadName = "Victoria St",
                                description = "Bugis Stn Exit B",
                                latitude = 1.30075679526626,
                                longitude = 103.85611040457583
                            ),
                        )
                    )
                }
            },
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertNull(
                repo.getBusStop(
                    busStopCode = "01060"
                )
            )
        }
    }

    @Test
    fun `set direct buses is stored in local storage`() {
        val localDataSource = FakeLocalDataSource()
        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setDirectBuses(
                listOf(
                    DirectBus(
                        sourceBusStopCode = "12345",
                        sourceBusStopDescription = "Source bus stop 12345",
                        destinationBusStopCode = "23456",
                        destinationBusStopDescription = "Destination bus stop 23456",
                        busServiceNumber = "961M",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBus(
                        sourceBusStopCode = "34567",
                        sourceBusStopDescription = "Source bus stop 34567",
                        destinationBusStopCode = "45678",
                        destinationBusStopDescription = "Destination bus stop v",
                        busServiceNumber = "77",
                        stops = 12,
                        distance = 11.13
                    ),
                )
            )

            assertEquals(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "961M",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "77",
                        stops = 12,
                        distance = 11.13
                    ),
                ),
                localDataSource.findAllDirectBuses()
            )
        }
    }

    @Test
    fun `set no direct buses is stored in local storage`() {
        val localDataSource = FakeLocalDataSource()
        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setNoDirectBusesFor(
                sourceBusStopCode = "12345",
                destinationBusStopCode = "23456",
            )

            assertEquals(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = false,
                        busServiceNumber = "no-service",
                        stops = -1,
                        distance = -1.0
                    ),
                ),
                localDataSource.findAllDirectBuses()
            )
        }
    }

    @Test
    fun `set direct buses is stored in local storage and removing old ones`() {
        val localDataSource = FakeLocalDataSource()

        runTest {
            localDataSource.insertDirectBuses(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "106",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "12",
                        stops = 12,
                        distance = 11.13
                    ),
                )
            )
        }

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setDirectBuses(
                listOf(
                    DirectBus(
                        sourceBusStopCode = "12345",
                        sourceBusStopDescription = "Source bus stop 12345",
                        destinationBusStopCode = "23456",
                        destinationBusStopDescription = "Destination bus stop 23456",
                        busServiceNumber = "961M",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBus(
                        sourceBusStopCode = "34567",
                        sourceBusStopDescription = "Source bus stop 34567",
                        destinationBusStopCode = "45678",
                        destinationBusStopDescription = "Destination bus stop v",
                        busServiceNumber = "77",
                        stops = 12,
                        distance = 11.13
                    ),
                )
            )

            assertEquals(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "961M",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "77",
                        stops = 12,
                        distance = 11.13
                    ),
                ),
                localDataSource.findAllDirectBuses()
            )
        }
    }

    @Test
    fun `set no direct buses is stored in local storage and removing old ones`() {
        val localDataSource = FakeLocalDataSource()

        runTest {
            localDataSource.insertDirectBuses(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "106",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "12",
                        stops = 12,
                        distance = 11.13
                    ),
                )
            )
        }

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setNoDirectBusesFor(
                sourceBusStopCode = "12345",
                destinationBusStopCode = "23456",
            )

            assertEquals(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "12",
                        stops = 12,
                        distance = 11.13
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = false,
                        busServiceNumber = "no-service",
                        stops = -1,
                        distance = -1.0
                    ),
                ),
                localDataSource.findAllDirectBuses()
            )
        }
    }

    @Test
    fun `get direct buses no cached yet`() {
        val localDataSource = FakeLocalDataSource()
        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertEquals(
                DirectBusesResult.NotCachedYet,
                repo.getDirectBuses(
                    sourceBusStopCode = "12345",
                    destinationBusStopCode = "23456",
                )
            )
        }
    }

    @Test
    fun `get direct buses no direct buses`() {
        val localDataSource = FakeLocalDataSource()
        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setNoDirectBusesFor(
                sourceBusStopCode = "12345",
                destinationBusStopCode = "23456"
            )
            assertEquals(
                DirectBusesResult.NoDirectBuses,
                repo.getDirectBuses(
                    sourceBusStopCode = "12345",
                    destinationBusStopCode = "23456",
                )
            )
        }
    }

    @Test
    fun `get direct buses - direct buses`() {
        val localDataSource = FakeLocalDataSource()

        runTest {
            localDataSource.insertBusStops(
                listOf(
                    BusStopEntity(
                        code = "12345",
                        roadName = "123456 road name",
                        description = "Source bus stop 12345",
                        latitude = 1.23,
                        longitude = 4.56
                    ),
                    BusStopEntity(
                        code = "23456",
                        roadName = "234567 road name",
                        description = "Destination bus stop 23456",
                        latitude = 1.23,
                        longitude = 4.56
                    )
                )
            )
        }

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setDirectBuses(
                listOf(
                    DirectBus(
                        sourceBusStopCode = "12345",
                        sourceBusStopDescription = "Source bus stop 12345",
                        destinationBusStopCode = "23456",
                        destinationBusStopDescription = "Destination bus stop 23456",
                        busServiceNumber = "961M",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBus(
                        sourceBusStopCode = "12345",
                        sourceBusStopDescription = "Source bus stop 12345",
                        destinationBusStopCode = "23456",
                        destinationBusStopDescription = "Destination bus stop 23456",
                        busServiceNumber = "77",
                        stops = 12,
                        distance = 11.13
                    ),
                )
            )
            assertEquals(
                DirectBusesResult.Success(
                    directBusList = listOf(
                        DirectBus(
                            sourceBusStopCode = "12345",
                            sourceBusStopDescription = "Source bus stop 12345",
                            destinationBusStopCode = "23456",
                            destinationBusStopDescription = "Destination bus stop 23456",
                            busServiceNumber = "961M",
                            stops = 17,
                            distance = 15.46
                        ),
                        DirectBus(
                            sourceBusStopCode = "12345",
                            sourceBusStopDescription = "Source bus stop 12345",
                            destinationBusStopCode = "23456",
                            destinationBusStopDescription = "Destination bus stop 23456",
                            busServiceNumber = "77",
                            stops = 12,
                            distance = 11.13
                        ),
                    )
                ),
                repo.getDirectBuses(
                    sourceBusStopCode = "12345",
                    destinationBusStopCode = "23456",
                )
            )
        }
    }

    @Test
    fun `get cached direct buses stop permutations count`() {
        val localDataSource = FakeLocalDataSource()

        runTest {
            localDataSource.insertDirectBuses(
                listOf(
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "106",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "77",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "12345",
                        destinationBusStopCode = "23456",
                        hasDirectBus = true,
                        busServiceNumber = "108",
                        stops = 17,
                        distance = 15.46
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "12",
                        stops = 12,
                        distance = 11.13
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "34567",
                        destinationBusStopCode = "45678",
                        hasDirectBus = true,
                        busServiceNumber = "9",
                        stops = 12,
                        distance = 11.13
                    ),
                    DirectBusEntity(
                        sourceBusStopCode = "45678",
                        destinationBusStopCode = "56789",
                        hasDirectBus = false,
                        busServiceNumber = "no-service",
                        stops = -1,
                        distance = -1.0
                    ),
                )
            )
        }

        val repo = BusStopRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource {
                ""
            },
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            assertEquals(
                3,
                repo.getCachedDirectBusesStopPermutationsCount()
            )
        }
    }
}