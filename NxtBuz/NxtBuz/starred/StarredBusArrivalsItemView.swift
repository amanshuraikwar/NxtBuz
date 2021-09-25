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
//        VStack(
//            spacing: 6
//        ) {
//            Text(starredBusArrivalItemData.starredBusArrival.busStopDescription)
//                .font(NxtBuzFonts.callout)
//                .foregroundColor(.primary)
            
            HStack {
                if let arriving = starredBusArrivalItemData.starredBusArrival.busArrivals as? BusArrivals.Arriving {
                    Image(getBusTypeImageName(busType: arriving.nextArrivingBus.type))
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.primary)
                    
                    ZStack {
                        Text(starredBusArrivalItemData.starredBusArrival.busServiceNumber)
                            .font(NxtBuzFonts.callout)
                            .foregroundColor(Color(.systemGray6))
                        
                        Text("961M")
                            .font(NxtBuzFonts.callout)
                            .opacity(0.0)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .background(Color.accentColor)
                    .clipShape(Capsule())
                    
                    Image(systemName: "chevron.right")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .foregroundColor(Color.primary)
                    
                    Text("\(arriving.nextArrivingBus.arrival)")
                        .font(NxtBuzFonts.callout)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                } else {
                    Image(getBusTypeImageName(busType: BusType.sd))
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.secondary)
                    
                    ZStack {
                        Text(starredBusArrivalItemData.starredBusArrival.busServiceNumber)
                            .font(NxtBuzFonts.callout)
                            .foregroundColor(Color(.systemGray6))
                        
                        Text("961M")
                            .font(NxtBuzFonts.callout)
                            .opacity(0.0)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .background(Color(.systemGray))
                    .clipShape(Capsule())
                    
                    Image(systemName: "chevron.right")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .foregroundColor(Color.primary)
                    
                    Text("N/A")
                        .font(NxtBuzFonts.callout)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                }
//            }
        }
//        .padding(8)
//        .background(Color(.systemGray6))
//        .cornerRadius(16)
    }
    
    private func getBusTypeImageName(busType: BusType) -> String {
        if (busType == BusType.dd) {
            return "BusTypeDd"
        } else if (busType == BusType.bd) {
            return "BusTypeFeeder"
        } else {
            return "BusTypeNormal"
        }
    }
}

//struct StarredBusArrivalsItemView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsItemView()
//    }
//}
