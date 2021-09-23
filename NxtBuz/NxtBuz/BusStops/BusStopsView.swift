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
    @Binding var bottomContentPadding: CGFloat?
    
    var body: some View {
        ZStack {
            switch viewModel.busStopsScreenState {
            case .Fetching:
                VStack {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle())
                    
                    Text("Fetching bus stops...")
                        .font(NxtBuzFonts.body)
                        .padding()
                }
                .padding(.bottom, bottomContentPadding)
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
                .padding(.bottom, bottomContentPadding)
            case .GoToSettingsLocationPermission:
                VStack(
                    spacing: 32
                ) {
                    Image(systemName: "location.slash.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 48, height: 48)
                        .foregroundColor(Color.accentColor)
                    
                    Text("We need location permission to get nearby bus stops :)")
                        .font(NxtBuzFonts.title3)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                        .padding(.horizontal)
                    
                    PrimaryButton(
                        text: "Go to Settings",
                        action: { UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!) },
                        iconSystemName: "chevron.forward"
                    ).padding(.horizontal)
                }
                .padding(.bottom, bottomContentPadding)
            case .AskLocationPermission:
                VStack(
                    spacing: 32
                ) {
                    Image(systemName: "location.slash.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 48, height: 48)
                        .foregroundColor(Color.accentColor)
                    
                    Text("We need location permission to get nearby bus stops :)")
                        .font(NxtBuzFonts.title3)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                        .padding(.horizontal)
                    
                    PrimaryButton(
                        text: "Give Permission",
                        action: { viewModel.requestPermission() },
                        iconSystemName: nil
                    ).padding(.horizontal)
                }
                .padding(.bottom, bottomContentPadding)
            case .Success(let busStopList):
                List {
                    Section(
                        header: Text("Nearby Bus Stops")
                            .font(NxtBuzFonts.caption),
                        // todo: this is a hack to add space at the bottom of the list, find a better way
                        footer: Spacer()
                            .frame(minHeight: bottomContentPadding)
                    ) {
                        ForEach(
                            Array(busStopList.enumerated()),
                            id: \.1
                        ) { index, busStop in
                            NavigationLink(
                                destination: BusStopArrivalsView(
                                    busStop: busStop,
                                    bottomContentPadding: $bottomContentPadding
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
                .id(UUID())
                .listStyle(InsetGroupedListStyle())
            }
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
