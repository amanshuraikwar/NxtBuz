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
    
    var body: some View {
        ZStack {
            switch viewModel.screenState {
            case .Fetching:
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                    
                    Text("Fetching arrivals...")
                        .font(NxtBuzFonts.body)
                        .padding()
                }
            case .Error(_):
                ErrorView(
                    systemName: "exclamationmark.icloud.fill",
                    errorMessage: "Something went wrong. Please try again.",
                    retryText: "Retry",
                    onRetry: {
                        viewModel.onRetryClick()
                    },
                    iconSystemName: nil
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
