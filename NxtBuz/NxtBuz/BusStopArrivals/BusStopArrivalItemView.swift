//
//  BusStopArrivalItemView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalItemView: View {
    let busStopArrival: BusStopArrival
    
    var body: some View {
        if let busArrivals = busStopArrival.busArrivals as? BusArrivals.Arriving {
            HStack(
                alignment: .top,
                spacing: 0
            ) {
                BusServiceView(
                    busServiceNumber: busStopArrival.busServiceNumber,
                    busType: busArrivals.nextArrivingBus.type
                )
                .padding(.vertical, 4)
                
                VStack(
                    alignment: .leading,
                    spacing: 0
                ) {
                    BusArrivalView(busArrivals: busArrivals)
                    
                    BusDestinationView(destination: busArrivals.nextArrivingBus.destination.busStopDescription)
                        .padding(.top, 2)
                }
                .padding(.horizontal)
                .padding(.vertical, 4)
            }
        } else {
            HStack(
                alignment: .center,
                spacing: 0
            ) {
                if let busArrivals = busStopArrival.busArrivals as? BusArrivals.DataNotAvailable {
                    BusServiceView(
                        busServiceNumber: busStopArrival.busServiceNumber
                    )
                    .padding(.vertical, 4)
                    
                    BusArrivalErrorView(busArrivals: busArrivals)
                        .padding(.horizontal)
                        .padding(.vertical, 4)
                }
                
                if let busArrivals = busStopArrival.busArrivals as? BusArrivals.NotOperating {
                    BusServiceView(
                        busServiceNumber: busStopArrival.busServiceNumber
                    )
                    .padding(.vertical, 4)
                    
                    BusArrivalErrorView(busArrivals: busArrivals)
                        .padding(.horizontal)
                        .padding(.vertical, 4)
                }
                
                if let busArrivals = busStopArrival.busArrivals as? BusArrivals.Error {
                    BusServiceView(
                        busServiceNumber: busStopArrival.busServiceNumber
                    )
                    .padding(.vertical, 4)
                    
                    BusArrivalErrorView(busArrivals: busArrivals)
                        .padding(.horizontal)
                        .padding(.vertical, 4)
                }
            }
        }
    }
}
