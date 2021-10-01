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
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        let view = ZStack {
            switch viewModel.busStopsScreenState {
            case .Fetching(let message):
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
                    
                    Text(message)
                        .font(NxtBuzFonts.body)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
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
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                            .font(NxtBuzFonts.caption)
                    ) {
                        if busStopList.isEmpty {
                            WarningBannerView(
                                message: "No bus stops found :(",
                                iconSystemName: "sun.haze.fill"
                            )
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
                            VStack(
                                spacing: 0
                            ) {
                                Text("Results might not be accurate due to location's low accuracy.")
                                    .multilineTextAlignment(.center)
                                    .font(NxtBuzFonts.title3)
                                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
                                    .frame(maxWidth: .infinity)
                                    .padding()
                                    .fixedSize(horizontal: false, vertical: true)
                                
                                Divider()
                                
                                SecondaryButton(text: "Give Precise Location Permission") {
                                    UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
                                }
                                
                                Divider()
                                
                                SecondaryButton(text: "Reload Bus Stops") {
                                    viewModel.fetchBusStops(showFetching: true)
                                }
                            }
                        }
                    }
                }
                .listStyle(InsetGroupedListStyle())
            }
        }
        .toolbar {
            Button(
                action: {
                    viewModel.fetchBusStops(showFetching: true)
                }
            ) {
                Image(systemName: "arrow.counterclockwise.circle.fill")
                    .imageScale(.medium)
                    .foregroundColor(Color(nxtBuzTheme.accentColor))
            }
        }
        .onChange(of: searchString) { searchString in
            viewModel.onSearch(searchString: searchString)
        }
        .onAppear {
            viewModel.fetchBusStops()
        }
        
        if #available(iOS 15.0, *) {
            view.refreshable {
                viewModel.fetchBusStops(showFetching: true)
            }
        } else {
            view
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
