//
//  BusArrivalView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI
import iosUmbrella

struct BusArrivalView: View {
    let busArrivalStr: String
    let busLoadImage: String
    let accessibleImage: String
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    init(busArrivals: BusArrivals.Arriving) {
        self.busArrivalStr = BusArrivalView.getBusArrivalStr(arrival: Int(busArrivals.nextArrivingBus.arrival))
        
        if busArrivals.nextArrivingBus.load == BusLoad.sea {
            self.busLoadImage = "BusLoad1"
        } else if busArrivals.nextArrivingBus.load == BusLoad.sda {
            self.busLoadImage = "BusLoad2"
        } else if busArrivals.nextArrivingBus.load == BusLoad.lsd {
            self.busLoadImage = "BusLoad3"
        } else {
            self.busLoadImage = "BusLoad0"
        }
        
        if busArrivals.nextArrivingBus.wheelchairAccess {
            self.accessibleImage = "Accessible"
        } else {
            self.accessibleImage = "NotAccessible"
        }
    }
    
    var body: some View {
        HStack {
            Text(busArrivalStr)
                .font(NxtBuzFonts.title2)
                .fontWeight(.bold)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
            
            Image(busLoadImage)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
            
            Image(accessibleImage)
                .renderingMode(.template)
                .resizable()
                .scaledToFit()
                .frame(width: 20, height: 20)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
        }
    }
    
    private static func getBusArrivalStr(arrival: Int) -> String {
        if (arrival >= 60) {
            return "60+ mins"
        } else if (arrival > 0) {
            return String(format: "%02d mins", arrival)
        } else {
            return "Arriving"
        }
    }
}

//struct BusArrivalView_Previews: PreviewProvider {
//    static var previews: some View {
//        Group {
//            BusArrivalView(
//                busArrivals: BusArrivals.Arriving.init(
//                    nextArrivingBus:
//                        ArrivingBus(
//                            origin: ArrivingBusStop(
//                                busStopCode: "123456",
//                                roadName: "This Road",
//                                busStopDescription: "Origin Bus Stop"
//                            ),
//                            destination: ArrivingBusStop(
//                                busStopCode: "123456",
//                                roadName: "This Road",
//                                busStopDescription: "Origin Bus Stop"
//                            ),
//                            arrival: 6,
//                            latitude: 1.2,
//                            longitude: 1.2,
//                            visitNumber: 1,
//                            load: BusLoad.lsd,
//                            wheelchairAccess: true,
//                            type: BusType.bd
//                        ),
//                    followingArrivingBusList: []
//                )
//            )
//            .padding()
//            .previewLayout(.sizeThatFits)
//            .preferredColorScheme(.light)
//            
//            BusArrivalView(
//                busArrivals: BusArrivals.Arriving.init(
//                    nextArrivingBus:
//                        ArrivingBus(
//                            origin: ArrivingBusStop(
//                                busStopCode: "123456",
//                                roadName: "This Road",
//                                busStopDescription: "Origin Bus Stop"
//                            ),
//                            destination: ArrivingBusStop(
//                                busStopCode: "123456",
//                                roadName: "This Road",
//                                busStopDescription: "Origin Bus Stop"
//                            ),
//                            arrival: 6,
//                            latitude: 1.2,
//                            longitude: 1.2,
//                            visitNumber: 1,
//                            load: BusLoad.sea,
//                            wheelchairAccess: false,
//                            type: BusType.bd
//                        ),
//                    followingArrivingBusList: []
//                )
//            )
//            .padding()
//            .previewLayout(.sizeThatFits)
//            .preferredColorScheme(.dark)
//        }
//    }
//}
