//
//  ArrivingBusWidgetView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 07/10/21.
//

import SwiftUI
import iosUmbrella

struct ArrivingBusWidgetView: View {
    let busStopCode: String
    let busStopDescription: String
    let busServiceNumber: String
    let nextArrivingBusData: ArrivingBusData
    let followingArrivingBusDataList: [ArrivingBusData]
    let updated: Date
    
    @EnvironmentObject private var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        GeometryReader { geometry in
            HStack(
                spacing: 0
            ) {
                VStack(
                    spacing: 0
                ) {
                    HStack {
                        BusServiceNumberView(busServiceNumber: busServiceNumber, error: false)
                        
                        Spacer()
                    }

                    Text(busStopDescription)
                        .font(NxtBuzFonts.footnote)
                        .fontWeight(.medium)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.top, 8)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)

                    
                    Spacer()
                    
                    Text("\(getTime(date: updated))")
                        .font(NxtBuzFonts.caption)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding(.vertical)
                .padding(.leading)
                .padding(.trailing, 8)
                .frame(width: geometry.size.width / 2.5)
                
                VStack(
                    spacing: 0
                ) {
                    ForEach(followingArrivingBusDataList) { arrivingBus in
                        HStack {
                            Image(getBusTypeImageName(arrivingBus.busType))
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                            
                            Spacer()
                            
                            Text(getTime(date: arrivingBus.nextArrivalTime))
                                .font(NxtBuzFonts.bodyMonospaced)
                                .fontWeight(.bold)
                            
                            Spacer()
                            
                            Image(getBusLoadImageName(arrivingBus.busLoad))
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                        }
                        .padding(.vertical, 2)
                        .padding(.horizontal, 4)
                        .background(Color(.white).opacity(0.2))
                        .cornerRadius(8)
                        .padding(.bottom, 4)
                    }
                    .foregroundColor(nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
                    
                    Spacer()
                    
//                    HStack {
//                        Spacer()
//                        
//                        Link(destination: URL(string: "busArrivalWidget://refreshBusStopArrivals?busStopCode=\(busStopCode)&busServiceNumber=\(busServiceNumber)")!) {
//                            Image(systemName: "arrow.triangle.2.circlepath")
//                                .resizable()
//                                .scaledToFit()
//                                .frame(width: 18, height: 18)
//                                .padding(6)
//                                .foregroundColor(Color(nxtBuzTheme.accentColor))
//                                .background(.white)
//                                .cornerRadius(12)
//                        }
//                    }
                }
                .foregroundColor(.white)
                .padding(.vertical)
                .padding(.trailing)
                .padding(.leading)
                .frame(width: geometry.size.width * 3 / 5)
                //.frame(width: geometry.size.height, alignment: .top)
                .background(Color(nxtBuzTheme.accentColor))
            }
        }
        .frame(maxWidth: .infinity)
    }
    
    public func getBusTypeImageName(_ busType: BusType) -> String {
        if (busType == BusType.dd) {
            return "BusTypeDd"
        } else if (busType == BusType.bd) {
            return "BusTypeFeeder"
        } else {
            return "BusTypeNormal"
        }
    }
    
    public func getBusLoadImageName(_ busLoad: BusLoad) -> String {
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
    
    public func getWheelCharAccessImageName(_ wheelchairAccess: Bool) -> String {
        if wheelchairAccess {
            return "Accessible"
        } else {
            return "NotAccessible"
        }
    }
    
    public func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}

//struct ArrivingBusWidgetView_Previews: PreviewProvider {
//    static var previews: some View {
//        ArrivingBusWidgetView()
//    }
//}
