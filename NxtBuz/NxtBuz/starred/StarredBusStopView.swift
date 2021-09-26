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
            header: Text(starredBusStop.busStopDescription)
                .font(NxtBuzFonts.body)
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
                        Label("Un Star", systemImage: "star.slash")
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
