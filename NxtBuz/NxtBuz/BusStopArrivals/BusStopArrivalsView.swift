//
//  BusStopArrivalsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalsView: View {
    let busStop: BusStop
    @StateObject private var viewModel = BusStopArrivalsViewModel()
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        ZStack {
            switch viewModel.screenState {
            case .Fetching:
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
                    
                    Text("Fetching arrivals...")
                        .font(NxtBuzFonts.body)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        .padding()
                }
            case .Error(_):
                ErrorView(
                    systemName: "exclamationmark.icloud.fill",
                    errorMessage: "Something went wrong. Please try again.",
                    retryText: "Retry",
                    onRetry: {
                        viewModel.onRetryClick()
                    }
                )
            case .Success(let data):
                BusStopArrivalsListView(
                    data: data,
                    busStop: busStop,
                    onStarToggle: { busServiceNumber, newValue in
                        viewModel.onStarToggle(
                            busServiceNumber: busServiceNumber,
                            newValue: newValue
                        )
                    }
                )
            }
        }
        .navigationBarItems(
            trailing: Button(
                action: {
                    if (UIApplication.shared.canOpenURL(URL(string:"comgooglemaps://")!)) {
                        UIApplication.shared.open(
                            NSURL(string: "comgooglemaps://?saddr=&daddr=\(Float(busStop.latitude)),\(Float(busStop.longitude))&directionsmode=walking")! as URL
                        )
                    } else if (UIApplication.shared.canOpenURL(URL(string:"maps://")!)) {
                        UIApplication.shared.open(
                            NSURL(string: "maps://?saddr=&daddr=\(Float(busStop.latitude)),\(Float(busStop.longitude))&directionsmode=walking")! as URL
                        )
                    }
                }
            ) {
                Image(systemName: "arrow.triangle.turn.up.right.circle.fill")
                    .imageScale(.medium)
                    .foregroundColor(Color(nxtBuzTheme.accentColor))
            }
        )
        .navigationBarTitle(
            Text(busStop.description_),
            displayMode: .automatic
        )
        .onAppear {
            viewModel.getArrivals(busStopCode: busStop.code)
        }
        .onDisappear {
            viewModel.stopArrivalsLoop()
        }
    }
}
