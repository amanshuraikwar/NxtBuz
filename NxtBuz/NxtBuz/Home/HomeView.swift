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
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    @State var searchString = ""
    
    var body: some View {
        switch viewModel.screenState {
        case HomeScreenState.Setup:
            SetupView(
                onSetupComplete: {
                    viewModel.getUserState()
                }
            )
        case HomeScreenState.BusStops:
            TabView(selection: $viewModel.tabSelection) {
                NxtBuzNavigationView(
                    AnyView(
                        BusStopsView(searchString: $searchString)
                            .navigationTitle("Next Bus SG")
                            .environmentObject(nxtBuzTheme)
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
                .tag(1)

                NxtBuzNavigationView(
                    AnyView(
                        StarredBusArrivalsView()
                            .navigationTitle("Starred Buses")
                            .environmentObject(nxtBuzTheme)
                    ),
                    title: "Starred Buses"
                )
                .ignoresSafeArea()
                .tabItem {
                    Label("Starred Buses", systemImage: "star.circle.fill")
                }
                .tag(2)

                NxtBuzNavigationView(
                    AnyView(
                        SettingsView()
                            .navigationTitle("Settings")
                            .environmentObject(nxtBuzTheme)
                    ),
                    title: "Settings"
                )
                .ignoresSafeArea()
                .tabItem {
                    Label("Settings", systemImage: "ellipsis.circle.fill")
                }
                .tag(3)
            }
            .onOpenURL { url in
                viewModel.onDeeplinkUrlOpen(url: url)
            }
            .alert(isPresented: $viewModel.showingAlert) {
                Alert(title: Text("Bus arrivals updated!"), dismissButton: .default(Text("Got it!")))
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
