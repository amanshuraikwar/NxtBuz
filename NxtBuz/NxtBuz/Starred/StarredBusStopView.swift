//
//  StarredBusStopView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 26/09/21.
//

import SwiftUI

struct StarredBusStopView: View {
    @StateObject var starredBusStop: StarredBusStop
    let onUnStarClick: (_ busStopCode: String, _ busServiceNumber: String) -> Void
    
    var body: some View {
        Section(
            header: NavigationLink (destination: Text("gello")) {
                Text(starredBusStop.busStopDescription)
                    .font(NxtBuzFonts.body)
            }
        ) {
            ForEach(starredBusStop.starredBusArrivalItemDataList) { starredBusArrivalItemData in
                StarredBusArrivalsItemView(
                    starredBusArrivalItemData: starredBusArrivalItemData
                ).contextMenu {
                    Button {
                        onUnStarClick(
                            starredBusArrivalItemData.starredBusArrival.busStopCode,
                            starredBusArrivalItemData.starredBusArrival.busServiceNumber
                        )
                    } label: {
                        Label("Delete", systemImage: "star.slash")
                            .font(NxtBuzFonts.body)
                    }
                }
            }
        }
    }
}

//struct StarredBusStopView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusStopView()
//    }
//}
