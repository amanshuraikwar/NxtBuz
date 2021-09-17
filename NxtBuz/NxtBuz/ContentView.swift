//
//  ContentView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 15/09/21.
//

import SwiftUI
import iosUmbrella
//import busstopdata
//import localdatasource

struct ContentView: View {
    @State var userState: UserState = UserState.New.init()
    @State var setupProgress = 0.0
    @State var busStopList = [BusStop]()
    
    var body: some View {
        let coroutineDispatcherProvider = DispatcherProviderFactory.init().getDispatcherProvider()
        let remoteDataSource = KtorRemoteDataSource.Companion().createInstance(
            ltaAccountKey: "yO8B1RhDRoesLHDACerOUg==",
            addLoggingInterceptors: true,
            ioDispatcher: coroutineDispatcherProvider.io
        )
        let localDataSource = SqlDelightLocalDataSource.Companion().createInstance(
            dbFactory: DbFactory(),
            ioDispatcher: coroutineDispatcherProvider.io
        )
        let preferenceStorage = SettingsFactory().createPreferenceStorage()
        let busStopRepository = BusStopRepository(
            localDataSource: localDataSource,
            remoteDataSource: remoteDataSource,
            preferenceStorage: preferenceStorage,
            dispatcherProvider: coroutineDispatcherProvider
        )
        let userRepository = UserRepository(
            preferenceStorage: preferenceStorage,
            dispatcherProvider: coroutineDispatcherProvider,
            systemThemeHelper: SystemThemeHelper()
        )
        let getUserStateUseCase = GetUserStateUseCase(
            userRepository: userRepository
        )
        let doSetupUseCase = DoSetupUseCase(
            userRepository: userRepository,
            busStopRepository: busStopRepository
        )
        let getBusStopsUseCase = GetBusStopsUseCase(
            busStopRepository: busStopRepository
        )
        
        if (userState is UserState.New) {
            VStack {
                Text("Setting up... \(setupProgress)")
                    .padding()
            }
            .onAppear {
                getUserStateUseCase.invoke { userState in
                    if (userState is UserState.New) {
                        // setup
                        DispatchQueue.main.async {
                            doSetupUseCase.invoke { setupState in
                                if (setupState is SetupState.InProgress) {
                                    self.setupProgress = (setupState as! SetupState.InProgress).progress
                                }
                            }
                        }
                    }
                    self.userState = userState
                }
            }
        } else {
            List {
                ForEach(Array(busStopList.enumerated()), id: \.1) { index, busStop in
                    VStack {
                        Text(busStop.description_)
                        Text(busStop.roadName)
                        Text("\(busStop.operatingBusList.count)")
                        ForEach(Array(busStop.operatingBusList.enumerated()), id: \.1) { index, operatingBus in
                            Text(operatingBus.serviceNumber)
                        }
                    }
                }
            }
            .onAppear {
                getBusStopsUseCase.invoke(
                    lat: 1.3416,
                    lon: 103.7757,
                    limit: 50
                ) { busStopList in
                    self.busStopList = busStopList
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
