//
//  StarredBusArrivalsItemView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 23/09/21.
//

import SwiftUI
import iosUmbrella

struct StarredBusArrivalsItemView: View {
    @StateObject var starredBusArrivalItemData: StarredBusArrivalItemData
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        VStack(
            alignment: .leading,
            spacing: 0
        ) {
            if let arriving = starredBusArrivalItemData.starredBusArrival.busArrivals as? BusArrivals.Arriving {
                HStack(
                    alignment: .top,
                    spacing: 0
                ) {
                    BusServiceNumberView(
                        busServiceNumber: starredBusArrivalItemData.starredBusArrival.busServiceNumber,
                        error: false
                    )
                    
                    VStack(
                        alignment: .leading,
                        spacing: 0
                    ) {
                        ArrivingBusView(
                            arrivingBus: arriving.nextArrivingBus
                        )
                        .padding(.top, 2)
                        
                        ForEach(arriving.followingArrivingBusList, id: \.self) { arrivingBus in
                            ArrivingBusView(
                                arrivingBus: arrivingBus
                            )
                            .padding(.top, 8)
                        }
                        
                        DestinationBusStopView(
                            busStopDescription: arriving.nextArrivingBus.destination.busStopDescription
                        )
                        .padding(.top, 8)
                    }
                    .padding(.horizontal)
                    
                }
                .padding(.vertical, 12)
            } else {
                HStack {
                    BusServiceNumberView(
                        busServiceNumber: starredBusArrivalItemData.starredBusArrival.busServiceNumber,
                        error: true
                    )
                    
                    Text("Not Arriving")
                        .font(NxtBuzFonts.title2)
                        .fontWeight(.bold)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                        .padding(.leading, 8)
                }
                .padding(.vertical, 12)
            }
        }
    }
}

//struct StarredBusArrivalsItemView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsItemView()
//    }
//}
