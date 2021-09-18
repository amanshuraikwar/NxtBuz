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
//    @State var userState: UserState = UserState.New.init()
//    @State var setupProgress = 0.0
    @State private var busStopList = [BusStop]()
//    @State private var searchText = ""
    
    @StateObject var viewModel = HomeViewModel()
    
    var body: some View {
        
//        let getBusStopsUseCase = GetBusStopsUseCase(
//            busStopRepository: busStopRepository
//        )

        switch viewModel.screenState {
            case HomeScreenState.Setup:
                SetupView(
                    onSetupComplete: {
                        viewModel.getUserState()
                    }
                )
            case HomeScreenState.BusStops:
                NavigationView {
                    BusStopsView(busStopList: $busStopList)
                        .navigationTitle("Next Bus SG")
                        .listStyle(GroupedListStyle())
                        .navigationBarItems(
                            trailing: Button(
                                action: {
                                    print("User icon pressed...")
                                }
                            ) {
                                Image(systemName: "gearshape.fill")
                                    .imageScale(.large)
                            }
                        )
                        .onAppear {
                            Di.get().getBusStopsUseCase().invoke(
                                lat: 1.3416,
                                lon: 103.7757,
                                limit: 50
                            ) { busStopList in
                                self.busStopList = busStopList
                            }
                        }
                }
            case HomeScreenState.Fetching:
                Text("Fetching...")
                    .onAppear {
                        viewModel.getUserState()
                    }
        }
//        if (viewModel.screenState == HomeScreenState.Fetching) {
//            Text("Fetching...")
//            SetupView()
//            VStack {
//                Text("Setting up... \(setupProgress)")
//                    .padding()
//            }
//            .onAppear {
//                getUserStateUseCase.invoke { userState in
//                    if (userState is UserState.New) {
//                        // setup
//                        DispatchQueue.main.async {
//                            doSetupUseCase.invoke { setupState in
//                                if (setupState is SetupState.InProgress) {
//                                    self.setupProgress = (setupState as! SetupState.InProgress).progress
//                                }
//                            }
//                        }
//                    }
//                    self.userState = userState
//                }
//            }
//        } else {
//
//        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
