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
        HStack {
            if let arriving = starredBusArrivalItemData.starredBusArrival.busArrivals as? BusArrivals.Arriving {
                ZStack {
                    Text(starredBusArrivalItemData.starredBusArrival.busServiceNumber)
                        .font(NxtBuzFonts.body)
                        .foregroundColor(Color(.systemGray6))
                    
                    Text("961M")
                        .font(NxtBuzFonts.body)
                        .opacity(0.0)
                }
                .padding(.vertical, 2)
                .padding(.horizontal, 4)
                .background(Color.accentColor)
                .clipShape(Capsule())
                
                Image(systemName: "chevron.right.circle.fill")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 12, height: 12)
                    .foregroundColor(Color.secondary)

                ScrollView(.horizontal, showsIndicators: false) {
                    HStack {
                        Text("\(getBusArrivalStr(Int(arriving.nextArrivingBus.arrival)))")
                                .font(NxtBuzFonts.body)
                                .fontWeight(.bold)
                            
                        Image(getBusTypeImageName(arriving.nextArrivingBus.type))
                            .renderingMode(.template)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(Color.primary)

                        Image(getBusLoadImageName(arriving.nextArrivingBus.load))
                            .renderingMode(.template)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(Color.primary)
                    
                
                        ForEach(arriving.followingArrivingBusList, id: \.self) { arrivingBus in
                            Image(systemName: "chevron.right.circle.fill")
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 12, height: 12)
                                .foregroundColor(Color.secondary)
                            
                            Text("\(getBusArrivalStr(Int(arrivingBus.arrival)))")
                                .font(NxtBuzFonts.body)
                                .fontWeight(.bold)
                            
                            Image(getBusTypeImageName(arrivingBus.type))
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                                .foregroundColor(Color.primary)

                            Image(getBusLoadImageName(arrivingBus.load))
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                                .foregroundColor(Color.primary)
                        }
                    }
                }
            } else {
                ZStack {
                    Text(starredBusArrivalItemData.starredBusArrival.busServiceNumber)
                        .font(NxtBuzFonts.body)
                        .foregroundColor(Color(.systemGray6))
                    
                    Text("961M")
                        .font(NxtBuzFonts.body)
                        .opacity(0.0)
                }
                .padding(.vertical, 2)
                .padding(.horizontal, 4)
                .background(Color(.systemGray))
                .clipShape(Capsule())
                
                Image(systemName: "chevron.right.circle.fill")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 12, height: 12)
                    .foregroundColor(Color.secondary)
                
                Text("Not Arriving")
                    .font(NxtBuzFonts.body)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
            }
        }
    }
    
    private func getBusTypeImageName(_ busType: BusType) -> String {
        if (busType == BusType.dd) {
            return "BusTypeDd"
        } else if (busType == BusType.bd) {
            return "BusTypeFeeder"
        } else {
            return "BusTypeNormal"
        }
    }
    
    private func getBusLoadImageName(_ busLoad: BusLoad) -> String {
        if busLoad == BusLoad.sea {
            return "BusLoad1"
        } else if busLoad == BusLoad.sda {
            return "BusLoad2"
        } else if busLoad == BusLoad.lsd {
            return "BusLoad3"
        } else {
            return "BusLoad0"
        }
    }
    
    private func getBusArrivalStr(_ arrival: Int) -> String {
        if (arrival >= 60) {
            return "60+"
        } else if (arrival > 0) {
            return String(format: "%02d", arrival)
        } else {
            return "NOW"
        }
    }
}

//struct StarredBusArrivalsItemView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsItemView()
//    }
//}
