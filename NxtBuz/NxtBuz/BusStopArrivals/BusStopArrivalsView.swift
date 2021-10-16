//
//  BusStopArrivalsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalsView: View {
    let busStopCode: String
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
            case .Success(let busStopCode, let data):
                BusStopArrivalsListView(
                    data: data,
                    busStopCode: busStopCode,
                    busStopRoadName: $viewModel.busStopRoadName,
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
                    viewModel.onNavigateClick()
                }
            ) {
                Image(systemName: "arrow.triangle.turn.up.right.circle.fill")
                    .imageScale(.medium)
                    .foregroundColor(Color(nxtBuzTheme.accentColor))
            }
        )
        .navigationBarTitle(
            Text(viewModel.busStopDescription),
            displayMode: .automatic
        )
        .onAppear {
            viewModel.getArrivals(busStopCode: busStopCode)
        }
        .onDisappear {
            viewModel.stopArrivalsLoop()
        }
    }
}
