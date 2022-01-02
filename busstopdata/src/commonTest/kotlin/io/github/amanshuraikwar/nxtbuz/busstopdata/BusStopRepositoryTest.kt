package io.github.amanshuraikwar.nxtbuz.busstopdata

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

    
}