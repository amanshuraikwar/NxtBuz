//
//  Di.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import Foundation
import iosUmbrella
import SwiftUI

class Di {
    private static let instance = Di()
    
    private static let coroutineDispatcherProvider = DispatcherProviderFactory.init().getDispatcherProvider()
    
    private static let remoteDataSource = KtorRemoteDataSource.Companion().createInstance(
        ltaAccountKey: "yO8B1RhDRoesLHDACerOUg==",
        addLoggingInterceptors: true,
        ioDispatcher: coroutineDispatcherProvider.io
    )
    
    private static let localDataSource = SqlDelightLocalDataSource.Companion().createInstance(
        dbFactory: DbFactory(dbBasePath: FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: "group.io.github.amanshuraikwar.NxtBuz")!.path),
        ioDispatcher: coroutineDispatcherProvider.io
    )
    
    private static let preferenceStorage = SettingsFactory(settingsSuiteName: "group.io.github.amanshuraikwar.NxtBuz").createPreferenceStorage()
    
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
    
    private static let searchRepository = SearchRepository(
        localDataSource: localDataSource,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let dynamoThemeRepository = DynamoThemeProvider().createDynamoThemeRepository(
        defaultTheme: DynamoTheme(
            darkThemeColors: DynamoThemeColors(
                primary: UIColor(.white),
                secondary: UIColor(.gray),
                accent: UIColor(.blue)
            ),
            lightThemeColors: DynamoThemeColors(
                primary: UIColor(.black),
                secondary: UIColor(.gray),
                accent: UIColor(.green)
            )
        ),
        enableThemeApiLogging: true,
        themeApiUrl: "https://amanshuraikwar.github.io/api/nxtBuzTheme.json"
    )
    
    private init() {}
    
    static func get() -> Di {
        return instance
    }
    
    private var navigatedBusStopCode: String? = nil
    
    func getNavigatedBusStopCode() -> String? {
        return navigatedBusStopCode
    }
    
    func setNavigatedBusStopCode(busStopCode: String) {
        navigatedBusStopCode = busStopCode
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
    
    func getSearchUseCase() -> SearchUseCase {
        return SearchUseCase(
            searchRepository: Di.searchRepository
        )
    }
    
    func getThemeUseCase() -> GetThemeUseCase {
        return GetThemeUseCase(dynamoThemeRepository: Di.dynamoThemeRepository)
    }
    
    func getUseSystemThemeUseCase() -> GetUseSystemThemeUseCase {
        GetUseSystemThemeUseCase(userRepository: Di.userRepository)
    }
    
    func getOperatingBusServicesUseCase() -> GetOperatingBusServicesUseCase {
        GetOperatingBusServicesUseCase(busArrivalRepository: Di.busArrivalRepository)
    }
    
    func setHomeBusStopUseCase() -> SetHomeBusStopUseCase {
        return SetHomeBusStopUseCase(
            repo: Di.userRepository
        )
    }
    
    func getHomeBusStopUseCase() -> GetHomeBusStopUseCase {
        return GetHomeBusStopUseCase(
            userRepository: Di.userRepository,
            busStopRepository: Di.busStopRepository
        )
    }
    
    func getNearbyGoingHomeBusesUseCase() -> GetNearbyGoingHomeBusesUseCase {
        return GetNearbyGoingHomeBusesUseCase(
            userRepository: Di.userRepository,
            busStopRepository: Di.busStopRepository,
            busRouteRepository: Di.busRouteRepository
        )
    }
}
