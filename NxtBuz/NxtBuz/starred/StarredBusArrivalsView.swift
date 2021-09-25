//
//  StarredBusArrivalsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 23/09/21.
//

import SwiftUI
import iosUmbrella

struct StarredBusArrivalsView: View {
    @StateObject private var viewModel = StarredBusArrivalsViewModel()
    
    var body: some View {
        ZStack {
            switch viewModel.screenState {
            case .Success(let data):
                StarredBusArrivalsListView(data: data)
            case .Fetching:
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                    
                    Text("Fetching starred bus arrivals...")
                        .font(NxtBuzFonts.body)
                        .padding()
                }
            case .Error(_):
                ErrorView(
                    systemName: "exclamationmark.icloud.fill",
                    errorMessage: "Something went wrong. Please try again.",
                    retryText: "Retry",
                    onRetry: {
                        //viewModel.onRetryClick()
                    },
                    iconSystemName: nil
                )
            }
        }
        .onAppear {
            viewModel.getArrivals()
        }
        .onDisappear {
            //viewModel.stopArrivalsLoop()
        }
    }
}

struct StarredBusArrivalsView_Previews: PreviewProvider {
    static var previews: some View {
        StarredBusArrivalsView()
    }
}
