package io.github.amanshuraikwar.nxtbuz.di.dagger

import android.app.Activity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.*
import io.github.amanshuraikwar.nxtbuz.domain.location.*
import io.github.amanshuraikwar.nxtbuz.domain.map.DefaultMapZoomUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.domain.search.SearchUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.*
import io.github.amanshuraikwar.nxtbuz.domain.user.*
import io.github.amanshuraikwar.nxtbuz.locationdata.LocationEmitter
import io.github.amanshuraikwar.nxtbuz.locationdata.LocationRepository
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Named

@Module
class UseCaseProvides {
    @Provides
    fun provideCleanupLocationUpdatesUseCase(
        locationEmitter: LocationEmitter
    ): CleanupLocationUpdatesUseCase {
        return CleanupLocationUpdatesUseCase(
            locationEmitter = locationEmitter
        )
    }

    @Provides
    fun provideDefaultLocationUseCase(
        locationRepository: LocationRepository
    ): DefaultLocationUseCase {
        return DefaultLocationUseCase(
            locationRepository = locationRepository
        )
    }

    @Provides
    fun provideGetLastKnownLocationUseCase(
        _activity: Activity,
        locationRepository: LocationRepository
    ): GetLastKnownLocationUseCase {
        return GetLastKnownLocationUseCase(
            _activity = _activity,
            locationRepository = locationRepository
        )
    }

    @Provides
    fun provideGetLocationAvailabilityUseCase(
        locationEmitter: LocationEmitter
    ): GetLocationAvailabilityUseCase {
        return GetLocationAvailabilityUseCase(
            locationEmitter = locationEmitter
        )
    }

    @Provides
    fun provideGetLocationSettingStateUseCase(
        _activity: Activity,
        locationRepository: LocationRepository
    ): GetLocationSettingStateUseCase {
        return GetLocationSettingStateUseCase(
            _activity = _activity,
            locationRepository = locationRepository
        )
    }

    @Provides
    fun provideGetLocationUpdatesUseCase(
        locationEmitter: LocationEmitter
    ): GetLocationUpdatesUseCase {
        return GetLocationUpdatesUseCase(
            locationEmitter = locationEmitter
        )
    }

    @Provides
    fun provideLocationPermissionDeniedPermanentlyUseCase(
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): LocationPermissionDeniedPermanentlyUseCase {
        return LocationPermissionDeniedPermanentlyUseCase(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Provides
    fun provideLocationPermissionStatusUseCase(
        locationEmitter: LocationEmitter
    ): LocationPermissionStatusUseCase {
        return LocationPermissionStatusUseCase(
            locationEmitter = locationEmitter
        )
    }

    @Provides
    fun providePushMapEventUseCase(
        @Named("mapScope") coroutineScope: CoroutineScope,
        @Named("mapEventFlow") mapEventFlow: MutableSharedFlow<MapEvent>
    ): PushMapEventUseCase {
        return PushMapEventUseCase(
            coroutineScope = coroutineScope,
            mapEventFlow = mapEventFlow
        )
    }

    @Provides
    fun provideGetThemeUseCase(
        userRepository: UserRepository
    ): GetThemeUseCase {
        return GetThemeUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideGetNearbyGoingHomeBusesUseCase(
        userRepository: UserRepository,
        busStopRepository: BusStopRepository,
        busRouteRepository: BusRouteRepository,
        busArrivalRepository: BusArrivalRepository
    ): GetNearbyGoingHomeBusesUseCase {
        return GetNearbyGoingHomeBusesUseCase(
            userRepository = userRepository,
            busStopRepository = busStopRepository,
            busRouteRepository = busRouteRepository,
            busArrivalRepository = busArrivalRepository
        )
    }

    @Provides
    fun provideMaxDistanceOfClosesBusStopUseCase(
        busStopRepository: BusStopRepository
    ): MaxDistanceOfClosesBusStopUseCase {
        return MaxDistanceOfClosesBusStopUseCase(
            busStopRepository = busStopRepository
        )
    }

    @Provides
    fun provideGetUserStateUseCase(
        userRepository: UserRepository
    ): GetUserStateUseCase {
        return GetUserStateUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideToggleBusStopStarUseCase(
        repo: StarredBusArrivalRepository
    ): ToggleBusStopStarUseCase {
        return ToggleBusStopStarUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideAlertStarredBusArrivalsMinutes(
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider
    ): AlertStarredBusArrivalsMinutes {
        return AlertStarredBusArrivalsMinutes(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Provides
    fun provideGetStarredBusServicesUseCase(
        repo: StarredBusArrivalRepository
    ): GetStarredBusServicesUseCase {
        return GetStarredBusServicesUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideSetForcedThemeUseCase(
        userRepository: UserRepository
    ): SetForcedThemeUseCase {
        return SetForcedThemeUseCase(
            userRepository = userRepository,
        )
    }

    @Provides
    fun provideDefaultMapZoomUseCase(): DefaultMapZoomUseCase {
        return DefaultMapZoomUseCase()
    }

    @Provides
    fun provideIsStarredUseCase(
        repo: StarredBusArrivalRepository
    ): IsStarredUseCase {
        return IsStarredUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideGetForcedThemeUseCase(
        userRepository: UserRepository
    ): GetForcedThemeUseCase {
        return GetForcedThemeUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSearchUseCase(
        searchRepository: SearchRepository,
    ): SearchUseCase {
        return SearchUseCase(
            searchRepository = searchRepository
        )
    }

    @Provides
    fun provideBusStopsQueryLimitUseCase(
        busStopRepository: BusStopRepository
    ): BusStopsQueryLimitUseCase {
        return BusStopsQueryLimitUseCase(
            busStopRepository = busStopRepository
        )
    }

    @Provides
    fun provideGetBusArrivalsUseCase(
        busArrivalRepository: BusArrivalRepository,
        starredBusArrivalRepository: StarredBusArrivalRepository
    ): GetBusArrivalsUseCase {
        return GetBusArrivalsUseCase(
            busArrivalRepository = busArrivalRepository,
            starredBusArrivalRepository = starredBusArrivalRepository
        )
    }

    @Provides
    fun provideDoSetupUseCase(
        userRepository: UserRepository,
        busStopRepository: BusStopRepository,
        busRouteRepository: BusRouteRepository
    ): DoSetupUseCase {
        return DoSetupUseCase(
            userRepository = userRepository,
            busStopRepository = busStopRepository,
            busRouteRepository = busRouteRepository
        )
    }

    @Provides
    fun provideShouldShowMapUseCase(
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider,
    ): ShouldShowMapUseCase {
        return ShouldShowMapUseCase(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Provides
    fun provideGetBusStopUseCase(
        busStopRepository: BusStopRepository
    ): GetBusStopUseCase {
        return GetBusStopUseCase(
            busStopRepository = busStopRepository,
        )
    }

    @Provides
    fun provideGetOperatingBusServicesUseCase(
        busArrivalRepository: BusArrivalRepository,
    ): GetOperatingBusServicesUseCase {
        return GetOperatingBusServicesUseCase(
            busArrivalRepository = busArrivalRepository
        )
    }

    @Provides
    fun provideSetHomeBusStopUseCase(
        repo: UserRepository
    ): SetHomeBusStopUseCase {
        return SetHomeBusStopUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideGetStarredBusArrivalsUseCase(
        getStarredBusServicesUseCase: GetStarredBusServicesUseCase,
        getBusArrivalsUseCase: GetBusArrivalsUseCase,
        getBusStopUseCase: GetBusStopUseCase,
        showErrorStarredBusArrivalsUseCase: ShowErrorStarredBusArrivalsUseCase,
    ): GetStarredBusArrivalsUseCase {
        return GetStarredBusArrivalsUseCase(
            getStarredBusServicesUseCase = getStarredBusServicesUseCase,
            getBusArrivalsUseCase = getBusArrivalsUseCase,
            getBusStopUseCase = getBusStopUseCase,
            showErrorStarredBusArrivalsUseCase = showErrorStarredBusArrivalsUseCase,
        )
    }

    @Provides
    fun provideGetBusRouteUseCase(
        busRouteRepository: BusRouteRepository
    ): GetBusRouteUseCase {
        return GetBusRouteUseCase(
            busRouteRepository = busRouteRepository
        )
    }

    @Provides
    fun provideGetUseSystemThemeUseCase(
        userRepository: UserRepository
    ): GetUseSystemThemeUseCase {
        return GetUseSystemThemeUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideGetBusStopsUseCase(
        busStopRepository: BusStopRepository
    ): GetBusStopsUseCase {
        return GetBusStopsUseCase(
            busStopRepository = busStopRepository
        )
    }

    @Provides
    fun provideAlertStarredBusArrivalsFrequency(
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider,
    ): AlertStarredBusArrivalsFrequency {
        return AlertStarredBusArrivalsFrequency(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider
        )
    }

    @Provides
    fun provideGetHomeBusStopUseCase(
        userRepository: UserRepository,
        busStopRepository: BusStopRepository
    ): GetHomeBusStopUseCase {
        return GetHomeBusStopUseCase(
            userRepository = userRepository,
            busStopRepository = busStopRepository,
        )
    }

    @Provides
    fun provideRefreshThemeUseCase(
        userRepository: UserRepository,
    ): RefreshThemeUseCase {
        return RefreshThemeUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideShouldAlertStarredBusArrivals(
        preferenceStorage: PreferenceStorage,
        dispatcherProvider: CoroutinesDispatcherProvider,
    ): ShouldAlertStarredBusArrivals {
        return ShouldAlertStarredBusArrivals(
            preferenceStorage = preferenceStorage,
            dispatcherProvider = dispatcherProvider,
        )
    }

    @Provides
    fun provideToggleStarUpdateUseCase(
        repo: StarredBusArrivalRepository
    ): ToggleStarUpdateUseCase {
        return ToggleStarUpdateUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideShowErrorStarredBusArrivalsUseCase(
        repo: StarredBusArrivalRepository
    ): ShowErrorStarredBusArrivalsUseCase {
        return ShowErrorStarredBusArrivalsUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideShouldStartPlayStoreReviewUseCase(
        userRepository: UserRepository
    ): ShouldStartPlayStoreReviewUseCase {
        return ShouldStartPlayStoreReviewUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSetUseSystemThemeUseCase(
        userRepository: UserRepository
    ): SetUseSystemThemeUseCase {
        return SetUseSystemThemeUseCase(
            userRepository = userRepository
        )
    }

    @Provides
    fun provideUpdatePlayStoreReviewTimeUseCase(
        userRepository: UserRepository
    ): UpdatePlayStoreReviewTimeUseCase {
        return UpdatePlayStoreReviewTimeUseCase(
            userRepository = userRepository
        )
    }
}