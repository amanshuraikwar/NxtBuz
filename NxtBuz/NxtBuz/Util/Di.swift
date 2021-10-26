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
    
    private static let coroutineDispatcherProvider = CoroutineProvides.shared.provideDispatcherProvider()
    
    private static let remoteDataSource = RemoteDataSourceProvides.shared.provideRemoteDataSource(
        ltaAccountKey: "yO8B1RhDRoesLHDACerOUg==",
        addLoggingInterceptors: true,
        coroutinesDispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let localDataSource = LocalDataSourceProvides.shared.provideLocalDataSource(
        localDataSourceParams: LocalDataSourceParams(
            dbBasePathName: FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: "group.io.github.amanshuraikwar.NxtBuz")!.path
        ),
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let preferenceStorage = PreferenceProvides.shared.providePreferenceStorage(
        preferenceStorageParams: PreferenceStorageParams(
            settingsSuiteName: "group.io.github.amanshuraikwar.NxtBuz"
        )
    )
    
    private static let userRepository = RepositoryProvides.shared.provideUserRepository(
        systemThemeHelper: SystemThemeHelper(),
        preferenceStorage: preferenceStorage,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let busRouteRepository = RepositoryProvides.shared.provideBusRouteRepository(
        localDataSource: localDataSource,
        remoteDataSource: remoteDataSource,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let busStopRepository = RepositoryProvides.shared.provideBusStopRepository(
        localDataSource: localDataSource,
        remoteDataSource: remoteDataSource,
        preferenceStorage: preferenceStorage,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let busArrivalRepository = RepositoryProvides.shared.provideBusArrivalRepository(
        localDataSource: localDataSource,
        remoteDataSource: remoteDataSource,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let starredBusArrivalRepository = RepositoryProvides.shared.provideStarredBusArrivalRepository(
        localDataSource: localDataSource,
        preferenceStorage: preferenceStorage,
        dispatcherProvider: coroutineDispatcherProvider
    )
    
    private static let searchRepository = RepositoryProvides.shared.provideSearchRepository(
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
    
    func getUserStateUserCase() -> IosGetUserStateUseCase {
        return IosGetUserStateUseCase(
            userRepository: Di.userRepository
        )
    }
    
    func getDoSetupUserStateUserCase() -> IosDoSetupUseCase {
        return IosDoSetupUseCase(
            userRepository: Di.userRepository,
            busStopRepository: Di.busStopRepository,
            busRouteRepository: Di.busRouteRepository
        )
    }
    
    func getBusStopsUseCase() -> IosGetBusStopsUseCase {
        return IosGetBusStopsUseCase(
            busStopRepository: Di.busStopRepository
        )
    }
    
    func getBusArrivalsUseCase() -> IosGetBusArrivalsUseCase {
        return IosGetBusArrivalsUseCase(
            busArrivalRepository: Di.busArrivalRepository,
            starredBusArrivalRepository: Di.starredBusArrivalRepository
        )
    }
    
    func getToggleBusStopStarUseCase() -> IosToggleBusStopStarUseCase {
        return IosToggleBusStopStarUseCase(repo: Di.starredBusArrivalRepository)
    }
    
    func getStarredBusServicesUseCase() -> GetStarredBusServicesUseCase {
        return GetStarredBusServicesUseCase(repo: Di.starredBusArrivalRepository)
    }
    
    func getBusStopUseCase() -> IosGetBusStopUseCase {
        return IosGetBusStopUseCase(busStopRepository: Di.busStopRepository)
    }
    
    func getShowErrorStarredBusArrivalsUseCase() -> ShowErrorStarredBusArrivalsUseCase {
        return ShowErrorStarredBusArrivalsUseCase(repo: Di.starredBusArrivalRepository)
    }
    
    func getStarredBusArrivalsUseCase() -> IosGetStarredBusArrivalsUseCase {
        return IosGetStarredBusArrivalsUseCase(
            getStarredBusServicesUseCase: getStarredBusServicesUseCase(),
            getBusArrivalsUseCase: getBusArrivalsUseCase(),
            getBusStopUseCase: getBusStopUseCase(),
            showErrorStarredBusArrivalsUseCase: getShowErrorStarredBusArrivalsUseCase()
        )
    }
    
    func getToggleStarUpdateUseCase() -> IosToggleStarUpdateUseCase {
        return IosToggleStarUpdateUseCase(
            repo: Di.starredBusArrivalRepository
        )
    }
    
    func getSearchUseCase() -> IosSearchUseCase {
        return IosSearchUseCase(
            searchRepository: Di.searchRepository
        )
    }
    
    func getThemeUseCase() -> GetDynamoThemeUseCase {
        return GetDynamoThemeUseCase(dynamoThemeRepository: Di.dynamoThemeRepository)
    }
    
    func getOperatingBusServicesUseCase() -> IosGetOperatingBusServicesUseCase {
        IosGetOperatingBusServicesUseCase(busArrivalRepository: Di.busArrivalRepository)
    }
    
    func setHomeBusStopUseCase() -> IosSetHomeBusStopUseCase {
        return IosSetHomeBusStopUseCase(
            repo: Di.userRepository
        )
    }
    
    func getHomeBusStopUseCase() -> IosGetHomeBusStopUseCase {
        return IosGetHomeBusStopUseCase(
            userRepository: Di.userRepository,
            busStopRepository: Di.busStopRepository
        )
    }
    
    func getNearbyGoingHomeBusesUseCase() -> IosGetNearbyGoingHomeBusesUseCase {
        return IosGetNearbyGoingHomeBusesUseCase(
            userRepository: Di.userRepository,
            busStopRepository: Di.busStopRepository,
            busRouteRepository: Di.busRouteRepository,
            busArrivalRepository: Di.busArrivalRepository
        )
    }
    
    func getCachedDirectBusDataUseCase() -> IosGetCachedDirectBusDataUseCase {
        return IosGetCachedDirectBusDataUseCase(
            busStopRepository: Di.busStopRepository
        )
    }
}
