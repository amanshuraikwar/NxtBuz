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
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        Section(
            header: NavigationLink (destination: BusStopArrivalsView(busStop: starredBusStop.busStop)) {
                HStack {
                    Text(starredBusStop.busStop.description_)
                        .font(NxtBuzFonts.body)
                        .fontWeight(.medium)
                    
                    Spacer()
                    
                    Image(systemName: "chevron.forward")
                }
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
                        Label("Remove", systemImage: "star.slash")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
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
