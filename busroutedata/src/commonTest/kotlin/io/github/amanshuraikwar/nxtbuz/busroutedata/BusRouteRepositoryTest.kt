@file:Suppress("IllegalIdentifier")

package io.github.amanshuraikwar.nxtbuz.busroutedata

import io.github.amanshuraikwar.nxtbuz.localdatasource.BusRouteEntity
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalHourMinute
import io.github.amanshuraikwar.nxtbuz.localdatasource.OperatingBusEntity
import io.github.amanshuraikwar.testutil.FakeCoroutinesDispatcherProvider
import io.github.amanshuraikwar.testutil.FakeLocalDataSource
import io.github.amanshuraikwar.testutil.FakeRemoteDataSource
import io.github.amanshuraikwar.testutil.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BusRouteRepositoryTest {
    @Test
    fun `setup populates the correct operating bus data in local storage, local storage is initially empty`() {

        val localDataSource = FakeLocalDataSource()

        val repo = BusRouteRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource { url ->
                if (
                    url.encodedPath.contains("BusRoutes")
                    && url.parameters["\$skip"]?.toInt() == 0
                ) {
                    """
                    {
                        "odata.metadata": "this is fake data",
                        "value": [
                            {
                                "ServiceNo": "10",
                                "Operator": "SBST",
                                "Direction": 1,
                                "StopSequence": 1,
                                "BusStopCode": "10000",
                                "Distance": 0,
                                "WD_FirstBus": "0500",
                                "WD_LastBus": "2300",
                                "SAT_FirstBus": "0500",
                                "SAT_LastBus": "2300",
                                "SUN_FirstBus": "0500",
                                "SUN_LastBus": "2300"
                            },
                            {
                                "ServiceNo": "10",
                                "Operator": "SBST",
                                "Direction": 1,
                                "StopSequence": 2,
                                "BusStopCode": "10001",
                                "Distance": 0.6,
                                "WD_FirstBus": "0502",
                                "WD_LastBus": "2302",
                                "SAT_FirstBus": "0502",
                                "SAT_LastBus": "2302",
                                "SUN_FirstBus": "0502",
                                "SUN_LastBus": "2302"
                            },
                            {
                                "ServiceNo": "10",
                                "Operator": "SBST",
                                "Direction": 1,
                                "StopSequence": 3,
                                "BusStopCode": "10002",
                                "Distance": 1.1,
                                "WD_FirstBus": "0504",
                                "WD_LastBus": "2304",
                                "SAT_FirstBus": "0504",
                                "SAT_LastBus": "2304",
                                "SUN_FirstBus": "0503",
                                "SUN_LastBus": "2304"
                            }
                        ]
                    }
                """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusRoutes")
                    && url.parameters["\$skip"]?.toInt() == 500
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                    "ServiceNo": "10",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 4,
                                    "BusStopCode": "10003",
                                    "Distance": 2.3,
                                    "WD_FirstBus": "0508",
                                    "WD_LastBus": "2308",
                                    "SAT_FirstBus": "0508",
                                    "SAT_LastBus": "2309",
                                    "SUN_FirstBus": "0507",
                                    "SUN_LastBus": "2308"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 1,
                                    "BusStopCode": "10003",
                                    "Distance": 0,
                                    "WD_FirstBus": "0530",
                                    "WD_LastBus": "2345",
                                    "SAT_FirstBus": "0530",
                                    "SAT_LastBus": "2345",
                                    "SUN_FirstBus": "0530",
                                    "SUN_LastBus": "2345"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 2,
                                    "BusStopCode": "10004",
                                    "Distance": 0.6,
                                    "WD_FirstBus": "0533",
                                    "WD_LastBus": "2348",
                                    "SAT_FirstBus": "0533",
                                    "SAT_LastBus": "2348",
                                    "SUN_FirstBus": "0532",
                                    "SUN_LastBus": "2348"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 3,
                                    "BusStopCode": "10005",
                                    "Distance": 0.9,
                                    "WD_FirstBus": "0534",
                                    "WD_LastBus": "2349",
                                    "SAT_FirstBus": "0534",
                                    "SAT_LastBus": "2349",
                                    "SUN_FirstBus": "0533",
                                    "SUN_LastBus": "2349"
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusRoutes")
                    && url.parameters["\$skip"]?.toInt() == 1000
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 4,
                                    "BusStopCode": "10006",
                                    "Distance": 1.2,
                                    "WD_FirstBus": "0535",
                                    "WD_LastBus": "2351",
                                    "SAT_FirstBus": "0535",
                                    "SAT_LastBus": "2350",
                                    "SUN_FirstBus": "0535",
                                    "SUN_LastBus": "2350"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 5,
                                    "BusStopCode": "10007",
                                    "Distance": 1.6,
                                    "WD_FirstBus": "0537",
                                    "WD_LastBus": "2353",
                                    "SAT_FirstBus": "0537",
                                    "SAT_LastBus": "2352",
                                    "SUN_FirstBus": "0536",
                                    "SUN_LastBus": "2352"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 1,
                                    "BusStopCode": "10004",
                                    "Distance": 0,
                                    "WD_FirstBus": "0515",
                                    "WD_LastBus": "2400",
                                    "SAT_FirstBus": "0515",
                                    "SAT_LastBus": "2400",
                                    "SUN_FirstBus": "0515",
                                    "SUN_LastBus": "2400"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 2,
                                    "BusStopCode": "10006",
                                    "Distance": 0.5,
                                    "WD_FirstBus": "0517",
                                    "WD_LastBus": "0002",
                                    "SAT_FirstBus": "0517",
                                    "SAT_LastBus": "0002",
                                    "SUN_FirstBus": "0517",
                                    "SUN_LastBus": "0002"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 3,
                                    "BusStopCode": "10008",
                                    "Distance": 0.9,
                                    "WD_FirstBus": "0518",
                                    "WD_LastBus": "0003",
                                    "SAT_FirstBus": "0518",
                                    "SAT_LastBus": "0003",
                                    "SUN_FirstBus": "0518",
                                    "SUN_LastBus": "0003"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 4,
                                    "BusStopCode": "10009",
                                    "Distance": 1.4,
                                    "WD_FirstBus": "0520",
                                    "WD_LastBus": "0005",
                                    "SAT_FirstBus": "0520",
                                    "SAT_LastBus": "0005",
                                    "SUN_FirstBus": "0520",
                                    "SUN_LastBus": "0005"
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusRoutes")
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
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setup().collect {
                println("bus route setup progress: $it")
            }

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10000",
                        busServiceNumber = "10",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 0),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 0),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 0),
                        satLastBus = LocalHourMinute(hour = 23, minute = 0),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 0),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 0),
                    )
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10000"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10001",
                        busServiceNumber = "10",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 2),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 2),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 2),
                        satLastBus = LocalHourMinute(hour = 23, minute = 2),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 2),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 2),
                    )
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10001"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10002",
                        busServiceNumber = "10",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 4),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 4),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 4),
                        satLastBus = LocalHourMinute(hour = 23, minute = 4),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 3),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 4),
                    )
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10002"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10003",
                        busServiceNumber = "10",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 8),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 8),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 8),
                        satLastBus = LocalHourMinute(hour = 23, minute = 9),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 7),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 8),
                    ),
                    OperatingBusEntity(
                        busStopCode = "10003",
                        busServiceNumber = "100",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 30),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 45),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 30),
                        satLastBus = LocalHourMinute(hour = 23, minute = 45),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 30),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 45),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10003"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10004",
                        busServiceNumber = "100",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 33),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 48),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 33),
                        satLastBus = LocalHourMinute(hour = 23, minute = 48),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 32),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 48),
                    ),
                    OperatingBusEntity(
                        busStopCode = "10004",
                        busServiceNumber = "101",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 15),
                        wdLastBus = LocalHourMinute(hour = 0, minute = 0),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 15),
                        satLastBus = LocalHourMinute(hour = 0, minute = 0),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 15),
                        sunLastBus = LocalHourMinute(hour = 0, minute = 0),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10004"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10005",
                        busServiceNumber = "100",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 34),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 49),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 34),
                        satLastBus = LocalHourMinute(hour = 23, minute = 49),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 33),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 49),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10005"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10006",
                        busServiceNumber = "100",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 35),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 51),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 35),
                        satLastBus = LocalHourMinute(hour = 23, minute = 50),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 35),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 50),
                    ),
                    OperatingBusEntity(
                        busStopCode = "10006",
                        busServiceNumber = "101",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 17),
                        wdLastBus = LocalHourMinute(hour = 0, minute = 2),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 17),
                        satLastBus = LocalHourMinute(hour = 0, minute = 2),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 17),
                        sunLastBus = LocalHourMinute(hour = 0, minute = 2),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10006"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10007",
                        busServiceNumber = "100",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 37),
                        wdLastBus = LocalHourMinute(hour = 23, minute = 53),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 37),
                        satLastBus = LocalHourMinute(hour = 23, minute = 52),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 36),
                        sunLastBus = LocalHourMinute(hour = 23, minute = 52),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10007"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10008",
                        busServiceNumber = "101",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 18),
                        wdLastBus = LocalHourMinute(hour = 0, minute = 3),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 18),
                        satLastBus = LocalHourMinute(hour = 0, minute = 3),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 18),
                        sunLastBus = LocalHourMinute(hour = 0, minute = 3),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10008"
                )
            )

            assertEquals(
                listOf(
                    OperatingBusEntity(
                        busStopCode = "10009",
                        busServiceNumber = "101",
                        wdFirstBus = LocalHourMinute(hour = 5, minute = 20),
                        wdLastBus = LocalHourMinute(hour = 0, minute = 5),
                        satFirstBus = LocalHourMinute(hour = 5, minute = 20),
                        satLastBus = LocalHourMinute(hour = 0, minute = 5),
                        sunFirstBus = LocalHourMinute(hour = 5, minute = 20),
                        sunLastBus = LocalHourMinute(hour = 0, minute = 5),
                    ),
                ),
                localDataSource.findOperatingBuses(
                    busStopCode = "10009"
                )
            )
        }
    }

    @Test
    fun `setup populates the correct bus route data in local storage, local storage is initially empty`() {
        val localDataSource = FakeLocalDataSource()

        val repo = BusRouteRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = FakeRemoteDataSource { url ->
                if (
                    url.encodedPath.contains("BusRoutes")
                    && url.parameters["\$skip"]?.toInt() == 0
                ) {
                    """
                    {
                        "odata.metadata": "this is fake data",
                        "value": [
                            {
                                "ServiceNo": "10",
                                "Operator": "SBST",
                                "Direction": 1,
                                "StopSequence": 1,
                                "BusStopCode": "10000",
                                "Distance": 0,
                                "WD_FirstBus": "0500",
                                "WD_LastBus": "2300",
                                "SAT_FirstBus": "0500",
                                "SAT_LastBus": "2300",
                                "SUN_FirstBus": "0500",
                                "SUN_LastBus": "2300"
                            },
                            {
                                "ServiceNo": "10",
                                "Operator": "SBST",
                                "Direction": 1,
                                "StopSequence": 2,
                                "BusStopCode": "10001",
                                "Distance": 0.6,
                                "WD_FirstBus": "0502",
                                "WD_LastBus": "2302",
                                "SAT_FirstBus": "0502",
                                "SAT_LastBus": "2302",
                                "SUN_FirstBus": "0502",
                                "SUN_LastBus": "2302"
                            },
                            {
                                "ServiceNo": "10",
                                "Operator": "SBST",
                                "Direction": 1,
                                "StopSequence": 3,
                                "BusStopCode": "10002",
                                "Distance": 1.1,
                                "WD_FirstBus": "0504",
                                "WD_LastBus": "2304",
                                "SAT_FirstBus": "0504",
                                "SAT_LastBus": "2304",
                                "SUN_FirstBus": "0503",
                                "SUN_LastBus": "2304"
                            }
                        ]
                    }
                """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusRoutes")
                    && url.parameters["\$skip"]?.toInt() == 500
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                    "ServiceNo": "10",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 4,
                                    "BusStopCode": "10003",
                                    "Distance": 2.3,
                                    "WD_FirstBus": "0508",
                                    "WD_LastBus": "2308",
                                    "SAT_FirstBus": "0508",
                                    "SAT_LastBus": "2309",
                                    "SUN_FirstBus": "0507",
                                    "SUN_LastBus": "2308"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 1,
                                    "BusStopCode": "10003",
                                    "Distance": 0,
                                    "WD_FirstBus": "0530",
                                    "WD_LastBus": "2345",
                                    "SAT_FirstBus": "0530",
                                    "SAT_LastBus": "2345",
                                    "SUN_FirstBus": "0530",
                                    "SUN_LastBus": "2345"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 2,
                                    "BusStopCode": "10004",
                                    "Distance": 0.6,
                                    "WD_FirstBus": "0533",
                                    "WD_LastBus": "2348",
                                    "SAT_FirstBus": "0533",
                                    "SAT_LastBus": "2348",
                                    "SUN_FirstBus": "0532",
                                    "SUN_LastBus": "2348"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 3,
                                    "BusStopCode": "10005",
                                    "Distance": 0.9,
                                    "WD_FirstBus": "0534",
                                    "WD_LastBus": "2349",
                                    "SAT_FirstBus": "0534",
                                    "SAT_LastBus": "2349",
                                    "SUN_FirstBus": "0533",
                                    "SUN_LastBus": "2349"
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusRoutes")
                    && url.parameters["\$skip"]?.toInt() == 1000
                ) {
                    """
                        {
                            "odata.metadata": "this is fake data",
                            "value": [
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 4,
                                    "BusStopCode": "10006",
                                    "Distance": 1.2,
                                    "WD_FirstBus": "0535",
                                    "WD_LastBus": "2351",
                                    "SAT_FirstBus": "0535",
                                    "SAT_LastBus": "2350",
                                    "SUN_FirstBus": "0535",
                                    "SUN_LastBus": "2350"
                                },
                                {
                                    "ServiceNo": "100",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 5,
                                    "BusStopCode": "10007",
                                    "Distance": 1.6,
                                    "WD_FirstBus": "0537",
                                    "WD_LastBus": "2353",
                                    "SAT_FirstBus": "0537",
                                    "SAT_LastBus": "2352",
                                    "SUN_FirstBus": "0536",
                                    "SUN_LastBus": "2352"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 1,
                                    "BusStopCode": "10004",
                                    "Distance": 0,
                                    "WD_FirstBus": "0515",
                                    "WD_LastBus": "2400",
                                    "SAT_FirstBus": "0515",
                                    "SAT_LastBus": "2400",
                                    "SUN_FirstBus": "0515",
                                    "SUN_LastBus": "2400"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 2,
                                    "BusStopCode": "10006",
                                    "Distance": 0.5,
                                    "WD_FirstBus": "0517",
                                    "WD_LastBus": "0002",
                                    "SAT_FirstBus": "0517",
                                    "SAT_LastBus": "0002",
                                    "SUN_FirstBus": "0517",
                                    "SUN_LastBus": "0002"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 3,
                                    "BusStopCode": "10008",
                                    "Distance": 0.9,
                                    "WD_FirstBus": "0518",
                                    "WD_LastBus": "0003",
                                    "SAT_FirstBus": "0518",
                                    "SAT_LastBus": "0003",
                                    "SUN_FirstBus": "0518",
                                    "SUN_LastBus": "0003"
                                },
                                {
                                    "ServiceNo": "101",
                                    "Operator": "SBST",
                                    "Direction": 1,
                                    "StopSequence": 4,
                                    "BusStopCode": "10009",
                                    "Distance": 1.4,
                                    "WD_FirstBus": "0520",
                                    "WD_LastBus": "0005",
                                    "SAT_FirstBus": "0520",
                                    "SAT_LastBus": "0005",
                                    "SUN_FirstBus": "0520",
                                    "SUN_LastBus": "0005"
                                }
                            ]
                        }
                    """.trimIndent()
                } else if (
                    url.encodedPath.contains("BusRoutes")
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
            dispatcherProvider = FakeCoroutinesDispatcherProvider
        )

        runTest {
            repo.setup().collect {
                println("bus route setup progress: $it")
            }

            assertEquals(
                listOf(
                    BusRouteEntity(
                        busServiceNumber = "10",
                        busStopCode = "10000",
                        direction = 1,
                        stopSequence = 1,
                        distance = 0.0,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "10",
                        busStopCode = "10001",
                        direction = 1,
                        stopSequence = 2,
                        distance = 0.6,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "10",
                        busStopCode = "10002",
                        direction = 1,
                        stopSequence = 3,
                        distance = 1.1,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "10",
                        busStopCode = "10003",
                        direction = 1,
                        stopSequence = 4,
                        distance = 2.3,
                    ),
                ),
                localDataSource.findBusRoute(
                    busServiceNumber = "10"
                )
            )

            assertEquals(
                listOf(
                    BusRouteEntity(
                        busServiceNumber = "100",
                        busStopCode = "10003",
                        direction = 1,
                        stopSequence = 1,
                        distance = 0.0,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "100",
                        busStopCode = "10004",
                        direction = 1,
                        stopSequence = 2,
                        distance = 0.6,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "100",
                        busStopCode = "10005",
                        direction = 1,
                        stopSequence = 3,
                        distance = 0.9,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "100",
                        busStopCode = "10006",
                        direction = 1,
                        stopSequence = 4,
                        distance = 1.2,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "100",
                        busStopCode = "10007",
                        direction = 1,
                        stopSequence = 5,
                        distance = 1.6,
                    ),
                ),
                localDataSource.findBusRoute(
                    busServiceNumber = "100"
                )
            )

            assertEquals(
                listOf(
                    BusRouteEntity(
                        busServiceNumber = "101",
                        busStopCode = "10004",
                        direction = 1,
                        stopSequence = 1,
                        distance = 0.0,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "101",
                        busStopCode = "10006",
                        direction = 1,
                        stopSequence = 2,
                        distance = 0.5,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "101",
                        busStopCode = "10008",
                        direction = 1,
                        stopSequence = 3,
                        distance = 0.9,
                    ),
                    BusRouteEntity(
                        busServiceNumber = "101",
                        busStopCode = "10009",
                        direction = 1,
                        stopSequence = 4,
                        distance = 1.4,
                    ),
                ),
                localDataSource.findBusRoute(
                    busServiceNumber = "101"
                )
            )
        }
    }
}