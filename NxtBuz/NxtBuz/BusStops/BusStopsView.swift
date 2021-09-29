//
//  BusStopsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopsView: View {
    @StateObject private var viewModel = BusStopsViewModel()
    @Binding var searchString: String
    
    var body: some View {
        ZStack {
            switch viewModel.busStopsScreenState {
            case .Fetching(let message):
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                    
                    Text(message)
                        .font(NxtBuzFonts.body)
                        .padding()
                }
            case .Error(let errorMessage):
                VStack(
                    spacing: 32
                ) {
                    Image(systemName: "xmark.octagon.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 32, height: 32)
                    
                    Text(errorMessage)
                        .font(NxtBuzFonts.body)
                        .fontWeight(.medium)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                        .padding(.horizontal)
                    
                    PrimaryButton(
                        text: "Retry",
                        action: { viewModel.fetchBusStops() },
                        iconSystemName: nil
                    ).padding(.horizontal)
                }
            case .GoToSettingsLocationPermission:
                ErrorView(
                    systemName: "location.slash.fill",
                    errorMessage: "We need location permission to get nearby bus stops :)",
                    retryText: "Go to Settings",
                    onRetry: {
                        UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
                    }
                )
            case .AskLocationPermission:
                ErrorView(
                    systemName: "location.slash.fill",
                    errorMessage: "We need location permission to get nearby bus stops :)",
                    retryText: "Give Permission",
                    onRetry: {
                        viewModel.requestPermission()
                    }
                )
            case .Success(let header, let busStopList, let searchResults, let lowAccuracy):
                List {
                    Section(
                        header: Text(header)
                            .font(NxtBuzFonts.caption)
                    ) {
                        if busStopList.isEmpty {
                            Text("No bus stops found :(")
                                .font(NxtBuzFonts.body)
                                .foregroundColor(.secondary)
                        } else {
                            ForEach(
                                Array(busStopList.enumerated()),
                                id: \.1
                            ) { index, busStop in
                                NavigationLink(
                                    destination: BusStopArrivalsView(
                                        busStop: busStop
                                    )
                                ) {
                                    BusStopItemView(
                                        busStopName: busStop.description_,
                                        roadName: busStop.roadName,
                                        busStopCode: busStop.code,
                                        operatingBusServiceNumbers: getOperatingBusStr(busStop.operatingBusList)
                                    )
                                }
                            }
                        }
                    }
                    
                    if !searchResults && lowAccuracy {
                        Section {
                            VStack {
                                Text("Results might not be accurate due to location's low accuracy.")
                                    .multilineTextAlignment(.center)
                                    .font(NxtBuzFonts.title3)
                                    .foregroundColor(.primary)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 8)
                                    .fixedSize(horizontal: false, vertical: true)
                                
                                Divider()
                                
                                Text("Give Precise Location Permission")
                                    .foregroundColor(.accentColor)
                                    .font(NxtBuzFonts.body)
                                    .fontWeight(.medium)
                                    .padding(8)
                                    .frame(maxWidth: .infinity)
                                    .onTapGesture {
                                        UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
                                    }
                                
                                Divider()
                                
                                Text("Reload Bus Stops")
                                    .foregroundColor(.accentColor)
                                    .font(NxtBuzFonts.body)
                                    .fontWeight(.medium)
                                    .padding(8)
                                    .frame(maxWidth: .infinity)
                                    .onTapGesture {
                                        viewModel.fetchBusStops(showFetching: true)
                                    }
                            }
                        }
                    }
                }
                .listStyle(InsetGroupedListStyle())
            }
        }
        .onChange(of: searchString) { searchString in
            viewModel.onSearch(searchString: searchString)
        }
        .onAppear {
            viewModel.fetchBusStops()
        }
    }
    
    func getOperatingBusStr(_ operatingBusList: [Bus]) -> String {
        var str = ""
        for index in 0...operatingBusList.count-1 {
            str += operatingBusList[index].serviceNumber
            if (index != operatingBusList.count-1) {
                str += "  "
            }
        }
        return str
    }
}
