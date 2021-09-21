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
    
    var body: some View {
        switch viewModel.screenState {
            case HomeScreenState.Setup:
                SetupView(
                    onSetupComplete: {
                        viewModel.getUserState()
                    }
                )
            case HomeScreenState.BusStops:
                NavigationView {
                    BusStopsView()
                        .navigationTitle("Next Bus SG")
                        .listStyle(GroupedListStyle())
                        .navigationBarItems(
                            trailing: Button(
                                action: {
                                    print("User icon pressed...")
                                }
                            ) {
                                Image(systemName: "gearshape.fill")
                                    .imageScale(.medium)
                            }
                        )
                }
            case HomeScreenState.Fetching:
                Text("Fetching...")
                    .onAppear {
                        viewModel.getUserState()
                    }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
