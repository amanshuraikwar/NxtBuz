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
    @Binding var bottomContentPadding: CGFloat?
    let busStop: BusStop
    var onStarToggle: (_ busServiceNumber: String, _ newValue: Bool) -> Void
    
    var body: some View {
        List {
            Section(
                header: Text("Info")
                    .font(NxtBuzFonts.caption)
            ) {
                Text(busStop.roadName + "  •  " + busStop.code)
                    .font(NxtBuzFonts.body)
            }
            
            if data.outdatedResults {
                HStack {
                    Image(systemName: "exclamationmark.icloud.fill")
                        .foregroundColor(Color.secondary)
                    
                    Text(
                        "Bus arrival times might be outdated."
                    )
                }
                .animation(.easeInOut, value: data.outdatedResults)
            }
            
            Section(
                header: Text("Bus Arrivals")
                    .font(NxtBuzFonts.caption),
                // todo: this is a hack to add space at the bottom of the list, find a better way
                footer: Text("Last updated on \(data.lastUpdatedOnStr)".uppercased())
                    .font(NxtBuzFonts.caption)
                    .frame(minHeight: bottomContentPadding, alignment: .top)
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
        }
        .listStyle(InsetGroupedListStyle())
    }
}

//struct BusStopArrivalsListView_Previews: PreviewProvider {
//    static var previews: some View {
//        BusStopArrivalsListView()
//    }
//}
