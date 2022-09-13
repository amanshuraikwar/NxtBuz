package io.github.amanshuraikwar.nxtbuz.di.dagger

import android.app.Activity
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.nxtbuz.common.model.map.MapEvent
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.domain.arrivals.GetBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busroute.GetBusRouteUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.BusStopsQueryLimitUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetNearbyGoingHomeBusesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetOperatingBusServicesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.GetStarredBusStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.MaxDistanceOfClosesBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.busstop.ToggleBusStopStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.DefaultLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLastKnownLocationUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationAvailabilityUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationSettingStateUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.GetLocationUpdatesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionDeniedPermanentlyUseCase
import io.github.amanshuraikwar.nxtbuz.domain.location.LocationPermissionStatusUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.DefaultMapZoomUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.PushMapEventUseCase
import io.github.amanshuraikwar.nxtbuz.domain.map.ShouldShowMapUseCase
import io.github.amanshuraikwar.nxtbuz.domain.search.SearchUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.AlertStarredBusArrivalsFrequency
import io.github.amanshuraikwar.nxtbuz.domain.starred.AlertStarredBusArrivalsMinutes
import io.github.amanshuraikwar.nxtbuz.domain.starred.GetStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.GetStarredBusServicesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.IsBusServiceStarredUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShouldAlertStarredBusArrivals
import io.github.amanshuraikwar.nxtbuz.domain.starred.ShowErrorStarredBusArrivalsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleBusServiceStarUseCase
import io.github.amanshuraikwar.nxtbuz.domain.starred.ToggleStarUpdateUseCase
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainDeparturesUseCase
import io.github.amanshuraikwar.nxtbuz.domain.train.GetTrainStopsUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.DoSetupUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetForcedThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetHomeBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetLaunchBusStopPageUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUseSystemThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.RefreshThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.SetForcedThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.SetHomeBusStopUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.SetLaunchBusStopPageUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.SetUseSystemThemeUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.ShouldStartPlayStoreReviewUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.UpdatePlayStoreReviewTimeUseCase
import io.github.amanshuraikwar.nxtbuz.locationdata.LocationEmitter
import io.github.amanshuraikwar.nxtbuz.locationdata.LocationRepository
import io.github.amanshuraikwar.nxtbuz.preferencestorage.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.repository.BusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusRouteRepository
import io.github.amanshuraikwar.nxtbuz.repository.BusStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.SearchRepository
import io.github.amanshuraikwar.nxtbuz.repository.StarredBusArrivalRepository
import io.github.amanshuraikwar.nxtbuz.repository.TrainStopRepository
import io.github.amanshuraikwar.nxtbuz.repository.UserRepository
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
    fun provideToggleBusServiceStarUseCase(
        repo: StarredBusArrivalRepository
    ): ToggleBusServiceStarUseCase {
        return ToggleBusServiceStarUseCase(
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
    ): IsBusServiceStarredUseCase {
        return IsBusServiceStarredUseCase(
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
        busRouteRepository: BusRouteRepository,
        starredBusArrivalRepository: StarredBusArrivalRepository
    ): DoSetupUseCase {
        return DoSetupUseCase(
            userRepository = userRepository,
            busStopRepository = busStopRepository,
            busRouteRepository = busRouteRepository,
            starredBusArrivalRepository = starredBusArrivalRepository,
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

    @Provides
    fun provideToggleBusStopStarUseCase(
        repo: BusStopRepository
    ): ToggleBusStopStarUseCase {
        return ToggleBusStopStarUseCase(
            repo = repo
        )
    }

    @Provides
    fun provideGetStarredBusStopsUseCase(
        repo: BusStopRepository
    ): GetStarredBusStopsUseCase {
        return GetStarredBusStopsUseCase(
            busStopRepository = repo
        )
    }

    @Provides
    fun provideGetLaunchBusStopPageUseCase(
        repo: UserRepository
    ): GetLaunchBusStopPageUseCase {
        return GetLaunchBusStopPageUseCase(
            userRepository = repo
        )
    }

    @Provides
    fun provideSetLaunchBusStopPageUseCase(
        repo: UserRepository
    ): SetLaunchBusStopPageUseCase {
        return SetLaunchBusStopPageUseCase(
            userRepository = repo
        )
    }

    @Provides
    fun provideGetTrainStopsUseCase(
        @Named("nsApi") nsApiTrainsStopRepository: TrainStopRepository
    ): GetTrainStopsUseCase {
        return GetTrainStopsUseCase(
            trainStopRepositories = listOf(nsApiTrainsStopRepository)
        )
    }

    @Provides
    fun provideGetTrainDeparturesUseCase(
        @Named("nsApi") nsApiTrainsStopRepository: TrainStopRepository
    ): GetTrainDeparturesUseCase {
        return GetTrainDeparturesUseCase(
            trainStopRepositories = listOf(nsApiTrainsStopRepository)
        )
    }
}