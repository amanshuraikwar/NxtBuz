//
//  StarredBusStopWidgetView.swift
//  StarredBusArrivalsWidgetExtension
//
//  Created by amanshu raikwar on 24/10/21.
//

import SwiftUI
import iosUmbrella

struct StarredBusStopWidgetView: View {
    static let MAX_DISPLAY_BUS_STOPS = 3
    static let MAX_DISPLAY_BUSES = 2
    
    let busStopData: StarredBusStopData
    let firstColumnWidth: CGFloat
    let secondColumnWidth: CGFloat
    
    @EnvironmentObject private var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack(
            alignment: .top,
            spacing: 0
        ) {
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                Text(busStopData.busStopDescription)
                    .font(NxtBuzFonts.footnote)
                    .fontWeight(.medium)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
                    .multilineTextAlignment(.leading)
                    .fixedSize(horizontal: false, vertical: true)
            }
            .padding(.leading)
            .padding(.trailing, 8)
            .frame(width: firstColumnWidth)
            
            
            VStack(
                spacing: 0
            ) {
                ForEach(busStopData.starredBusArrivalList, id: \.self) { starredBusArrival in
                    if let arriving = starredBusArrival.busArrivals as? BusArrivals.Arriving {
                        HStack {
                            Image(getBusTypeImageName(arriving.nextArrivingBus.type))
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                            
                            ZStack {
                                Text(starredBusArrival.busServiceNumber)
                                    .font(NxtBuzFonts.callout)
                                    .fontWeight(.bold)
                                
                                Text("961M")
                                    .font(NxtBuzFonts.callout)
                                    .fontWeight(.bold)
                                    .opacity(0)
                            }
                            .background(Color(nxtBuzTheme.accentColor))
                            .clipShape(Capsule())
                            
                            Spacer()
                            
                            Text(getTime(date: arriving.nextArrivingBus.arrivalInstant.toNSDate()))
                                .font(NxtBuzFonts.calloutMonospaced)
                                .fontWeight(.bold)
                        }
                        .padding(.vertical, 2)
                        .padding(.horizontal, 4)
                        .background(Color(.white).opacity(0.2))
                        .cornerRadius(8)
                        .padding(.bottom, 4)
                    }
                }
            }
            .foregroundColor(nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
            .padding(.leading)
            .padding(.trailing)
            .frame(width: secondColumnWidth)
        }
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
        timeFormatter.dateFormat = "h:mm"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}

//struct StarredBusStopWidgetView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusStopWidgetView()
//    }
//}
