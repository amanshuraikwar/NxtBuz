//
//  HomeView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 29/09/21.
//

import SwiftUI
import iosUmbrella

// Added iosUmbrella manually
// ref: https://betterprogramming.pub/migrating-an-existing-xcode-project-to-a-new-kotlin-multiplatform-mobile-app-b71d07f23b7a
struct HomeView: View {
    @StateObject var viewModel = HomeViewModel()
    @State private var showSearch = false
    @State private var searchString = ""
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        switch viewModel.screenState {
        case HomeScreenState.Setup:
            SetupView(
                onSetupComplete: {
                    viewModel.getUserState()
                }
            )
        case HomeScreenState.BusStops:
            TabView {
                NxtBuzNavigationView(
                    AnyView(
                        BusStopsView(searchString: $searchString)
                            .navigationTitle("Next Bus SG")
                    ),
                    title: "Next Bus SG",
                    searchPlaceholder: "Search Bus Stops...",
                    onSearch: { searchString in
                        self.searchString = searchString
                    },
                    onCancel: {
                        self.searchString = ""
                    }
                )
                .ignoresSafeArea()
                
                .tabItem {
                    Label(  "Home", systemImage: "house.circle.fill")
                }

                NxtBuzNavigationView(
                    AnyView(
                        StarredBusArrivalsView()
                            .navigationTitle("Starred Buses")
                    ),
                    title: "Starred Buses"
                )
                .ignoresSafeArea()
                .tabItem {
                    Label("Starred Buses", systemImage: "star.circle.fill")
                }

                NxtBuzNavigationView(
                    AnyView(
                        SettingsView()
                            .navigationTitle("Settings")
                    ),
                    title: "Settings"
                )
                .ignoresSafeArea()
                .tabItem {
                    Label("Settings", systemImage: "ellipsis.circle.fill")
                }
            }
        case HomeScreenState.Fetching:
            ProgressView()
                .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
                .onAppear {
                    viewModel.getUserState()
                }
        }
    }
}

//struct HomeView_Previews: PreviewProvider {
//    static var previews: some View {
//        HomeView()
//    }
//}
