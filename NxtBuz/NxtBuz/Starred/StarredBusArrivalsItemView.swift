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
    
    var body: some View {
        VStack(
            alignment: .leading,
            spacing: 0
        ) {
            if let arriving = starredBusArrivalItemData.starredBusArrival.busArrivals as? BusArrivals.Arriving {
                HStack {
                    BusServiceNumberView(
                        busServiceNumber: starredBusArrivalItemData.starredBusArrival.busServiceNumber,
                        error: false
                    )
                    
                    DestinationBusStopView(
                        busStopDescription: arriving.nextArrivingBus.destination.busStopDescription
                    )
                    .padding(.leading, 8)
                }
                .padding(.top, 12)
                
                VStack(
                    spacing: 0
                ) {
                    HStack {
                        ZStack(
                            alignment: .trailing
                        ) {
                            BusServiceNumberView(
                                busServiceNumber: "961M",
                                error: false
                            ).opacity(0.0)
                            
                            Image(systemName: "arrow.turn.down.right")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                                .foregroundColor(Color.secondary)
                                .padding(6)
                        }
                        
                        ArrivingBusView(
                            arrivingBus: arriving.nextArrivingBus
                        ).padding(.leading, 8)
                    }
                    .padding(.top, 16)
                    
                    ForEach(arriving.followingArrivingBusList, id: \.self) { arrivingBus in
                        HStack {
                            ZStack(
                                alignment: .trailing
                            ) {
                                BusServiceNumberView(
                                    busServiceNumber: "961M",
                                    error: false
                                ).opacity(0.0)
                                
                                Image(systemName: "arrow.turn.down.right")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 16, height: 16)
                                    .foregroundColor(Color.secondary)
                                    .padding(6)
                            }
                            
                            ArrivingBusView(
                                arrivingBus: arrivingBus
                            ).padding(.leading, 8)
                        }
                        .padding(.top, 12)
                    }
                }
                .padding(.bottom, 12)
            } else {
                HStack {
                    BusServiceNumberView(
                        busServiceNumber: starredBusArrivalItemData.starredBusArrival.busServiceNumber,
                        error: true
                    )
                    
                    Text("Not Arriving")
                        .font(NxtBuzFonts.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.secondary)
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
