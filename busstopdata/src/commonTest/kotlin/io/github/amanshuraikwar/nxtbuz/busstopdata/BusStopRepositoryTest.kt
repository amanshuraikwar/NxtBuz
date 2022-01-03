package io.github.amanshuraikwar.nxtbuz.busstopdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.BusStop
import io.github.amanshuraikwar.nxtbuz.localdatasource.BusStopEntity
import io.github.amanshuraikwar.testutil.FakeCoroutinesDispatcherProvider
import io.github.amanshuraikwar.testutil.FakeLocalDataSource
import io.github.amanshuraikwar.testutil.FakePreferenceStorage
import io.github.amanshuraikwar.testutil.FakeRemoteDataSource
import io.github.amanshuraikwar.testutil.runTest
import kotlinx.coroutines.flow.collect
import kotlin.test.Test
import kotlin.test.assertEquals

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
}