package io.github.amanshuraikwar.nxtbuz.userdata

import io.github.amanshuraikwar.nxtbuz.commonkmm.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.commonkmm.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.commonkmm.SystemThemeHelper
import io.github.amanshuraikwar.nxtbuz.commonkmm.user.UserState
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

expect fun runTest(block: suspend () -> Unit)

class UserRepositoryTest {
    private val fakePreferenceStorage = object : PreferenceStorage {
        override var onboardingCompleted: Boolean = false

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
        override var showMap: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}
        override var permissionDeniedPermanently: Boolean
            get() = TODO("Not yet implemented")
            set(value) {}

        override var theme: NxtBuzTheme = NxtBuzTheme.DARK

        override var useSystemTheme: Boolean = true

        override var playStoreReviewTimeMillis: Long = -1L

        override var homeBusStopCode: String
            get() = TODO("Not yet implemented")
            set(value) {}
    }

    private val fakeCoroutinesDispatcherProvider = CoroutinesDispatcherProvider(
        main = Dispatchers.Default,
        computation = Dispatchers.Default,
        io = Dispatchers.Default,
        pool8 = Dispatchers.Default,
        map = Dispatchers.Default,
        arrivalService = Dispatchers.Default,
        location = Dispatchers.Default,
    )

    @SpyK
    private var fakeSystemThemeHelper: SystemThemeHelper = object : SystemThemeHelper {
        override fun isSystemInDarkTheme(): Boolean {
            return true
        }
    }

    lateinit var userRepo: UserRepository

    @BeforeTest
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        userRepo = UserRepositoryImpl(
            preferenceStorage = fakePreferenceStorage,
            dispatcherProvider = fakeCoroutinesDispatcherProvider,
            systemThemeHelper = fakeSystemThemeHelper
        )
    }

    @Test
    fun `Default user state should be New`() {
        runTest {
            assertTrue(
                "Default user state should be New"
            ) {
                userRepo.getUserState() == UserState.New
            }
        }
    }

    @Test
    fun `User state should be SetupComplete after marking complete`() {
        runTest {
            userRepo.markSetupComplete()

            assertTrue(
                "User state should be SetupComplete after marking complete"
            ) {
                userRepo.getUserState() == UserState.SetupComplete
            }
        }
    }

    @Test
    fun `User state should not be SetupComplete after marking incomplete`() {
        runTest {
            userRepo.markSetupIncomplete()

            assertTrue(
                "User state should not be SetupComplete after marking incomplete"
            ) {
                userRepo.getUserState() != UserState.SetupComplete
            }
        }
    }

    @Test
    fun `Should start play store review after 1 week`() {
        runTest {
            val instant = mockk<Instant>()
            every { instant.toEpochMilliseconds() } returns 0L

            mockkObject(Clock.System)
            every { Clock.System.now() } returns instant

            userRepo.updatePlayStoreReviewTime()

            // 7 days' + 1 millis from zero millis
            every { instant.toEpochMilliseconds() } returns 1000 * 60 * 60 * 24 * 7L + 1

            assertTrue(
                "Should start play store review after 1 week"
            ) {
                userRepo.shouldStartPlayStoreReview()
            }
        }
    }

    @Test
    fun `Should start play store review at 1 week`() {
        runTest {
            val instant = mockk<Instant>()
            every { instant.toEpochMilliseconds() } returns 0L

            mockkObject(Clock.System)
            every { Clock.System.now() } returns instant

            userRepo.updatePlayStoreReviewTime()

            // 7 days' millis from zero millis
            every { instant.toEpochMilliseconds() } returns 1000 * 60 * 60 * 24 * 7L

            assertTrue(
                "Should start play store review after 1 week"
            ) {
                userRepo.shouldStartPlayStoreReview()
            }
        }
    }

    @Test
    fun `Should not start play store review before 1 week`() {
        runTest {
            val instant = mockk<Instant>()
            every { instant.toEpochMilliseconds() } returns 0L

            mockkObject(Clock.System)
            every { Clock.System.now() } returns instant

            userRepo.updatePlayStoreReviewTime()

            // 7 days' - 1 millis from zero millis
            every { instant.toEpochMilliseconds() } returns 1000 * 60 * 60 * 24 * 7L - 1

            assertTrue(
                "Should start play store review after 1 week"
            ) {
                !userRepo.shouldStartPlayStoreReview()
            }
        }
    }

    @Test
    fun `Should not start play store review when setup is not complete`() {
        runTest {
            userRepo.markSetupIncomplete()

            val instant = mockk<Instant>()

            // 7 days' + 1 millis from zero millis
            every { instant.toEpochMilliseconds() } returns 1000 * 60 * 60 * 24 * 7L + 1

            mockkObject(Clock.System)
            every { Clock.System.now() } returns instant

            assertTrue(
                "Should not start play store review when setup is not complete"
            ) {
                !userRepo.shouldStartPlayStoreReview()
            }
        }
    }

    @Test
    fun `Should start play store review when setup is complete and 1 week has passed`() {
        runTest {
            val instant = mockk<Instant>()
            every { instant.toEpochMilliseconds() } returns 0L

            mockkObject(Clock.System)
            every { Clock.System.now() } returns instant

            userRepo.markSetupComplete()

            // 7 days' + 1 millis from zero millis
            every { instant.toEpochMilliseconds() } returns 1000 * 60 * 60 * 24 * 7L + 1

            assertTrue(
                "Should start play store review when setup is complete and 1 week has passed"
            ) {
                userRepo.shouldStartPlayStoreReview()
            }
        }
    }

    @Test
    fun `Return system theme when use system theme is true`() {
        runTest {
            every { fakeSystemThemeHelper.isSystemInDarkTheme() } returns true

            userRepo.setUseSystemTheme(true)

            assertTrue(
                "Return system theme when use system theme is true"
            ) {
                userRepo.getTheme() == NxtBuzTheme.DARK
            }

            every { fakeSystemThemeHelper.isSystemInDarkTheme() } returns false

            userRepo.refreshTheme()

            assertTrue(
                "Return system theme when use system theme is true"
            ) {
                userRepo.getTheme() == NxtBuzTheme.LIGHT
            }
        }
    }

    @Test
    fun `Return forced theme when use system theme is false`() {
        runTest {
            userRepo.setUseSystemTheme(false)

            userRepo.setForcedTheme(NxtBuzTheme.DARK)

            assertTrue(
                "Return forced theme when use system theme is false"
            ) {
                userRepo.getTheme() == NxtBuzTheme.DARK
            }

            userRepo.setForcedTheme(NxtBuzTheme.LIGHT)

            userRepo.refreshTheme()

            assertTrue(
                "Return forced theme when use system theme is false"
            ) {
                userRepo.getTheme() == NxtBuzTheme.LIGHT
            }
        }
    }
}