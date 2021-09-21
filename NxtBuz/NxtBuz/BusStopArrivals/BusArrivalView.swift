//
//  BusArrivalView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI
import iosUmbrella

struct BusArrivalView: View {
    let busArrivals: BusArrivals.Arriving
    let busArrivalStr: String
    
    init(busArrivals: BusArrivals.Arriving) {
        self.busArrivals = busArrivals
        self.busArrivalStr = BusArrivalView.getBusArrivalStr(arrival: Int(busArrivals.nextArrivingBus.arrival))
    }
    
    var body: some View {
        Text(busArrivalStr)
            .font(NxtBuzFonts.title2)
            .fontWeight(.bold)
    }
    
    private static func getBusArrivalStr(arrival: Int) -> String {
        if (arrival >= 60) {
            return "60+ mins"
        } else if (arrival > 0) {
            return String(format: "%02d mins", arrival)
        } else {
            return "Arriving Now"
        }
    }
}

struct BusArrivalView_Previews: PreviewProvider {
    static var previews: some View {
        BusArrivalView(
            busArrivals: BusArrivals.Arriving.init(
                nextArrivingBus:
                    ArrivingBus(
                        origin: ArrivingBusStop(
                            busStopCode: "123456",
                            roadName: "This Road",
                            busStopDescription: "Origin Bus Stop"
                        ),
                        destination: ArrivingBusStop(
                            busStopCode: "123456",
                            roadName: "This Road",
                            busStopDescription: "Origin Bus Stop"
                        ),
                        arrival: 6,
                        latitude: 1.2,
                        longitude: 1.2,
                        visitNumber: 1,
                        load: BusLoad.lsd,
                        wheelchairAccess: true,
                        type: BusType.bd
                    ),
                followingArrivingBusList: []
            )
        )
    }
}
