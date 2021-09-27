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
                    StarredBusServiceView(
                        busServiceNumber: starredBusArrivalItemData.starredBusArrival.busServiceNumber,
                        error: false
                    )
                    
                    HStack(
                        spacing: 0
                    ) {
                        Image(systemName: "arrow.right.circle.fill")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(Color.secondary)
                            .padding(6)
                        
                        Text(arriving.nextArrivingBus.destination.busStopDescription)
                            .font(NxtBuzFonts.body)
                            .foregroundColor(.secondary)
                            .padding(.trailing, 6)
                    }
                    .background(Color(.systemGray5))
                    .cornerRadius(8)
                }
                .padding(.top, 8)
                
                VStack(
                    spacing: 0
                ) {
                    HStack {
                        StarredBusServiceView(
                            busServiceNumber: "961M",
                            error: false
                        ).opacity(0.0)
                        
                        StarredArrivingBusView(
                            arrivingBus: arriving.nextArrivingBus
                        )
                    }
                    .padding(.top, 12)
                    
                    ForEach(arriving.followingArrivingBusList, id: \.self) { arrivingBus in
                        HStack {
                            StarredBusServiceView(
                                busServiceNumber: "961M",
                                error: false
                            ).opacity(0.0)
                            
                            StarredArrivingBusView(
                                arrivingBus: arrivingBus
                            )
                        }
                        .padding(.top, 8)
                    }
                }
                .padding(.bottom, 8)
            } else {
                HStack {
                    StarredBusServiceView(
                        busServiceNumber: starredBusArrivalItemData.starredBusArrival.busServiceNumber,
                        error: true
                    )
                    
                    Text("Not Arriving")
                        .font(NxtBuzFonts.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.secondary)
                }
                .padding(.vertical, 8)
            }
        }
    }
    
   
}

//struct StarredBusArrivalsItemView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsItemView()
//    }
//}
