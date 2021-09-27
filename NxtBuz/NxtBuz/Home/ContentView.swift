//
//  ContentView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 15/09/21.
//

import SwiftUI
import iosUmbrella

// Added iosUmbrella manually
// ref: https://betterprogramming.pub/migrating-an-existing-xcode-project-to-a-new-kotlin-multiplatform-mobile-app-b71d07f23b7a
struct ContentView: View {
    @StateObject var viewModel = HomeViewModel()
    @State private var showSearch = false
    @State private var searchString = ""
    
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
                    SearchNavigationView(
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
                        Label("Home", systemImage: "house.fill")
                    }
                    
                    NavigationView {
                        StarredBusArrivalsView()
                            .navigationTitle("Starred Buses")
                    }
                    .tabItem {
                        Label("Starred Buses", systemImage: "list.star")
                    }
                    
                    SettingsView()
                        .tabItem {
                            Label("Settings", systemImage: "gearshape.fill")
                        }
                }
            case HomeScreenState.Fetching:
                Text("Fetching...")
                    .onAppear {
                        viewModel.getUserState()
                    }
        }
    }
}

struct StarredBusesLayoutHeightPreferenceKey: PreferenceKey {
    static let defaultValue: CGFloat = 0

    static func reduce(
        value: inout CGFloat,
        nextValue: () -> CGFloat
    ) {
        value = max(value, nextValue())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
