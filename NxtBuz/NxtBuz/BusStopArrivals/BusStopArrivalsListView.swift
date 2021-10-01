//
//  BusStopArrivalsListView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalsListView: View {
    @StateObject var data: BusStopArrivalScreenSuccessData
    let busStop: BusStop
    var onStarToggle: (_ busServiceNumber: String, _ newValue: Bool) -> Void
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        List {
            Section(
                header: Text("Info")
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            ) {
                Text(busStop.roadName + "  •  " + busStop.code)
                    .font(NxtBuzFonts.body)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
            }
            
            if data.outdatedResults {
                WarningBannerView(
                    message: "Bus arrival times might be outdated.",
                    iconSystemName: "exclamationmark.icloud.fill"
                )
            }
            
            Section(
                header: Text("Bus Arrivals")
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            ) {
                ForEach(data.busStopArrivalItemDataList) { busStopArrivalItemData in
                    BusStopArrivalItemView(
                        busStopArrivalItemData: busStopArrivalItemData,
                        onStarToggle: { newValue in
                            onStarToggle(
                                busStopArrivalItemData.busStopArrival.busServiceNumber,
                                newValue
                            )
                        }
                    )
                }
            }
            
            Text("Last updated on \(data.lastUpdatedOnStr)")
                .font(NxtBuzFonts.body)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
        }
        .listStyle(InsetGroupedListStyle())
    }
}

//struct BusStopArrivalsListView_Previews: PreviewProvider {
//    static var previews: some View {
//        BusStopArrivalsListView()
//    }
//}
