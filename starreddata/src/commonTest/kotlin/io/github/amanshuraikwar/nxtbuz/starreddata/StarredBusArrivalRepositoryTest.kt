@file:Suppress("IllegalIdentifier")

package io.github.amanshuraikwar.nxtbuz.starreddata

import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.StarredBusService
import io.github.amanshuraikwar.nxtbuz.commonkmm.starred.ToggleStarUpdate
import io.github.amanshuraikwar.nxtbuz.localdatasource.LocalDataSource
import io.github.amanshuraikwar.nxtbuz.localdatasource.StarredBusStopEntity
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import io.github.amanshuraikwar.testutil.FakeCoroutinesDispatcherProvider
import io.github.amanshuraikwar.testutil.FakeLocalDataSource
import io.github.amanshuraikwar.testutil.FakePreferenceStorage
import io.github.amanshuraikwar.testutil.joinWithExpiry
import io.github.amanshuraikwar.testutil.runTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StarredBusArrivalRepositoryTest {
    private val fakeCoroutinesDispatcherProvider = FakeCoroutinesDispatcherProvider

    lateinit var repo: StarredBusArrivalRepository

    @MockK
    lateinit var localDataSource: LocalDataSource

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        repo = StarredBusArrivalRepositoryImpl(
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = fakeCoroutinesDispatcherProvider,
            localDataSource = localDataSource
        )
    }

    @Test
    fun `should show error starred bus arrivals toggle update`() {
        runTest {
            var deferred = launch {
                assertEquals(false, repo.toggleShouldShowErrorArrivals.first())
            }
            repo.setShouldShowErrorStarredBusArrivals(false)
            deferred.joinWithExpiry(3000)
            assertEquals(false, repo.shouldShowErrorStarredBusArrivals())

            deferred = launch {
                assertEquals(true, repo.toggleShouldShowErrorArrivals.first())
            }
            repo.setShouldShowErrorStarredBusArrivals(true)
            deferred.joinWithExpiry(3000)
            assertEquals(true, repo.shouldShowErrorStarredBusArrivals())
        }
    }

    @Test
    fun `getting correct stored starred bus services`() {
        runTest {
            coEvery { localDataSource.findAllStarredBuses() } returns listOf(
                StarredBusStopEntity(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                ),
                StarredBusStopEntity(
                    busStopCode = "11401",
                    busServiceNumber = "61"
                ),
                StarredBusStopEntity(
                    busStopCode = "11379",
                    busServiceNumber = "145"
                ),
                StarredBusStopEntity(
                    busStopCode = "11181",
                    busServiceNumber = "111"
                ),
                StarredBusStopEntity(
                    busStopCode = "11379",
                    busServiceNumber = "200"
                ),
            )

            assertEquals(
                listOf(
                    StarredBusService(
                        busStopCode = "11401",
                        busServiceNumber = "106"
                    ),
                    StarredBusService(
                        busStopCode = "11401",
                        busServiceNumber = "61"
                    ),
                    StarredBusService(
                        busStopCode = "11379",
                        busServiceNumber = "145"
                    ),
                    StarredBusService(
                        busStopCode = "11181",
                        busServiceNumber = "111"
                    ),
                    StarredBusService(
                        busStopCode = "11379",
                        busServiceNumber = "200"
                    ),
                ),
                repo.getStarredBusServices()
            )
        }
    }

    @Test
    fun `toggle update bus stop star update from not starred to starred`() {
        runTest {
            coEvery {
                localDataSource.findStarredBus(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            } returns null

            val deferred = launch {
                assertEquals(
                    ToggleStarUpdate(
                        busStopCode = "11401",
                        busServiceNumber = "106",
                        newStarState = true
                    ),
                    repo.toggleStarUpdate.first()
                )
            }

            repo.toggleBusStopStar(
                busStopCode = "11401",
                busServiceNumber = "106"
            )

            deferred.joinWithExpiry(3000)
        }
    }

    @Test
    fun `toggle update bus stop star update from starred to not starred`() {
        runTest {
            coEvery {
                localDataSource.findStarredBus(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            } returns StarredBusStopEntity(
                busStopCode = "11401",
                busServiceNumber = "106"
            )

            val deferred = launch {
                assertEquals(
                    ToggleStarUpdate(
                        busStopCode = "11401",
                        busServiceNumber = "106",
                        newStarState = false
                    ),
                    repo.toggleStarUpdate.first()
                )
            }

            repo.toggleBusStopStar(
                busStopCode = "11401",
                busServiceNumber = "106"
            )

            deferred.joinWithExpiry(3000)
        }
    }

    @Test
    fun `toggleTo update bus stop star update from not starred to starred`() {
        runTest {
            coEvery {
                localDataSource.findStarredBus(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            } returns null

            val deferred = launch {
                assertEquals(
                    ToggleStarUpdate(
                        busStopCode = "11401",
                        busServiceNumber = "106",
                        newStarState = true
                    ),
                    repo.toggleStarUpdate.first()
                )
            }

            repo.toggleBusStopStar(
                busStopCode = "11401",
                busServiceNumber = "106",
                toggleTo = true
            )

            deferred.joinWithExpiry(3000)
        }
    }

    @Test
    fun `toggleTo update bus stop star update from starred to not starred`() {
        runTest {
            coEvery {
                localDataSource.findStarredBus(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            } returns StarredBusStopEntity(
                busStopCode = "11401",
                busServiceNumber = "106"
            )

            val deferred = launch {
                assertEquals(
                    ToggleStarUpdate(
                        busStopCode = "11401",
                        busServiceNumber = "106",
                        newStarState = false
                    ),
                    repo.toggleStarUpdate.first()
                )
            }

            repo.toggleBusStopStar(
                busStopCode = "11401",
                busServiceNumber = "106",
                toggleTo = false
            )

            deferred.joinWithExpiry(3000)
        }
    }

    @Test
    fun `is starred true`() {
        runTest {
            coEvery {
                localDataSource.findStarredBus(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            } returns StarredBusStopEntity(
                busStopCode = "11401",
                busServiceNumber = "106"
            )

            assertEquals(
                true,
                repo.isStarred(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            )
        }
    }

    @Test
    fun `is starred false`() {
        runTest {
            coEvery {
                localDataSource.findStarredBus(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            } returns null

            assertEquals(
                false,
                repo.isStarred(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            )
        }
    }

    @Test
    fun `toggle star bus stop to true and get correct toggle star value`() {
        val localDataSource = FakeLocalDataSource()

        val repo = StarredBusArrivalRepositoryImpl(
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = fakeCoroutinesDispatcherProvider,
            localDataSource = localDataSource
        )

        runTest {
            repo.toggleBusStopStar(
                busStopCode = "11401",
                busServiceNumber = "106",
                toggleTo = true
            )

            assertEquals(
                expected = true,
                actual = repo.isStarred(
                    busStopCode = "11401",
                    busServiceNumber = "106"
                )
            )
        }
    }

    @Test
    fun `toggle star multiple bus stops and get correct starred buses`() {
        val localDataSource = FakeLocalDataSource()

        val repo = StarredBusArrivalRepositoryImpl(
            preferenceStorage = FakePreferenceStorage(),
            dispatcherProvider = fakeCoroutinesDispatcherProvider,
            localDataSource = localDataSource
        )

        runTest {
            repo.toggleBusStopStar(
                busStopCode = "11401",
                busServiceNumber = "106",
                toggleTo = true
            )

            repo.toggleBusStopStar(
                busStopCode = "11406",
                busServiceNumber = "961",
                toggleTo = true
            )

            assertEquals(
                expected = listOf(
                    StarredBusService(
                        busStopCode = "11401",
                        busServiceNumber = "106",
                    ),
                    StarredBusService(
                        busStopCode = "11406",
                        busServiceNumber = "961",
                    )
                ),
                actual = repo.getStarredBusServices()
            )
        }
    }
}