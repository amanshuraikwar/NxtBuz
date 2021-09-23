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
    @Binding var bottomContentPadding: CGFloat?
    
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
                .padding(.bottom, bottomContentPadding)
            case .Success(let busStopArrivalItemDataList, _):
                List {
                    Section(
                        header: Text("Info")
                            .font(NxtBuzFonts.caption)
                    ) {
                        Text(busStop.roadName + "  •  " + busStop.code)
                            .font(NxtBuzFonts.body)
                    }
                    
                    Section(
                        header: Text("Bus Arrivals")
                            .font(NxtBuzFonts.caption),
                        // todo: this is a hack to add space at the bottom of the list, find a better way
                        footer: Text("Last updated on \(viewModel.lastUpdatedOn)".uppercased())
                            .font(NxtBuzFonts.caption)
                            .frame(minHeight: bottomContentPadding, alignment: .top)
                    ) {
                        ForEach(busStopArrivalItemDataList) { busStopArrivalItemData in
                            BusStopArrivalItemView(
                                busStopArrivalItemData: busStopArrivalItemData,
                                onStarToggle: { newValue in
                                    viewModel.onStarToggle(
                                        busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber,
                                        newValue: newValue
                                    )
                                }
                            )
                        }
                    }
                }
                .listStyle(InsetGroupedListStyle())
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
