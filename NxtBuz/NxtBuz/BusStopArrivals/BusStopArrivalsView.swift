//
//  BusStopArrivalsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 20/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalsView: View {
    private let busStop: BusStop
    @StateObject private var viewModel = BusStopArrivalsViewModel()
    
    init(busStop: BusStop) {
        self.busStop = busStop
    }
    
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
            case .Success(let busStopArrivalItemDataList, _):
                List {
                    Section(
                        header: Text("Bus Arrivals")
                            .font(NxtBuzFonts.caption),
                        footer: Text("Last updated on \(viewModel.lastUpdatedOn)".uppercased())
                            .font(NxtBuzFonts.caption)
                    ) {
                        ForEach(busStopArrivalItemDataList) { busStopArrivalItemData in
                            BusStopArrivalItemView(busStopArrivalItemData: busStopArrivalItemData)
                        }
                    }
                }
                .listStyle(InsetGroupedListStyle())
            }
        }
        .navigationBarTitle(
            Text(busStop.description_),
            displayMode: .inline
        )
        .onAppear {
            viewModel.getArrivals(busStopCode: busStop.code)
        }
        .onDisappear {
            viewModel.stopArrivalsLoop()
        }
    }
}
