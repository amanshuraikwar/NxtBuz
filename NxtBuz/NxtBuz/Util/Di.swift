//
//  Di.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import Foundation
import iosUmbrella

class Di {
    private static let instance = Di()
    
    private static let coroutineDispatcherProvider = DispatcherProviderFactory.init().getDispatcherProvider()
    
    private static let remoteDataSource = KtorRemoteDataSource.Companion().createInstance(
        ltaAccountKey: "yO8B1RhDRoesLHDACerOUg==",
        addLoggingInterceptors: true,
        ioDispatcher: coroutineDispatcherProvider.io
    )
    
    private static let localDataSource = SqlDelightLocalDataSource.Companion().createInstance(
        dbFactory: DbFactory(),
        ioDispatcher: coroutineDispatcherProvider.io
    )
    
    private static let preferenceStorage = SettingsFactory().createPreferenceStorage()
    
    private static let userRepository = UserRepository(
        preferenceStorage: preferenceStorage,
        dispatcherProvider: coroutineDispatcherProvider,
        systemThemeHelper: SystemThemeHelper()
    )
    
    private static let busRouteRepository = BusRouteRepository(
        localDataSource: localDataSource,
        remoteDataSource: remoteDataSource,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let busStopRepository = BusStopRepository(
        localDataSource: localDataSource,
        remoteDataSource: remoteDataSource,
        preferenceStorage: preferenceStorage,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let busArrivalRepository = BusArrivalRepository(
        localDataSource: localDataSource,
        remoteDataSource: remoteDataSource,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let starredBusArrivalRepository = StarredBusArrivalRepository(
        localDataSource: localDataSource,
        preferenceStorage: preferenceStorage,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private init() {}
    
    static func get() -> Di {
        return instance
    }
    
    func getUserStateUserCase() -> GetUserStateUseCase {
        return GetUserStateUseCase(
            userRepository: Di.userRepository
        )
    }
    
    func getDoSetupUserStateUserCase() -> DoSetupUseCase {
        return DoSetupUseCase(
            userRepository: Di.userRepository,
            busStopRepository: Di.busStopRepository,
            busRouteRepository: Di.busRouteRepository
        )
    }
    
    func getBusStopsUseCase() -> GetBusStopsUseCase {
        return GetBusStopsUseCase(
            busStopRepository: Di.busStopRepository
        )
    }
    
    func getBusArrivalsUseCase() -> GetBusArrivalsUseCase {
        return GetBusArrivalsUseCase(
            busArrivalRepository: Di.busArrivalRepository,
            starredBusArrivalRepository: Di.starredBusArrivalRepository
        )
    }
    
    func getToggleBusStopStarUseCase() -> ToggleBusStopStarUseCase {
        return ToggleBusStopStarUseCase(repo: Di.starredBusArrivalRepository)
    }
    
    func getStarredBusServicesUseCase() -> GetStarredBusServicesUseCase {
        return GetStarredBusServicesUseCase(repo: Di.starredBusArrivalRepository)
    }
    
    func getBusStopUseCase() -> GetBusStopUseCase {
        return GetBusStopUseCase(busStopRepository: Di.busStopRepository)
    }
    
    func getStarredBusArrivalsUseCase() -> GetStarredBusArrivalsUseCase {
        return GetStarredBusArrivalsUseCase(
            getStarredBusServicesUseCase: getStarredBusServicesUseCase(),
            getBusArrivalsUseCase: getBusArrivalsUseCase(),
            getBusStopUseCase: getBusStopUseCase()
        )
    }
    
    func getToggleStarUpdateUseCase() -> ToggleStarUpdateUseCase {
        return ToggleStarUpdateUseCase(
            repo: Di.starredBusArrivalRepository
        )
    }
}
