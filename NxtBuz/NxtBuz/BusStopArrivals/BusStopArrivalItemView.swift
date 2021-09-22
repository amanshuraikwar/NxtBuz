//
//  BusStopArrivalItemView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalItemView: View {
    @StateObject var busStopArrivalItemData: BusStopArrivalItemData
    
    var body: some View {
        if let busArrivals = busStopArrivalItemData.busStopArrival.busArrivals as? BusArrivals.Arriving {
            HStack(
                alignment: .top,
                spacing: 0
            ) {
                BusServiceView(
                    busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber,
                    busType: busArrivals.nextArrivingBus.type
                )
                .padding(.vertical, 4)
                
                VStack(
                    alignment: .leading,
                    spacing: 0
                ) {
                    BusArrivalView(
                        busArrivals: busArrivals
                    ).animation(.easeInOut, value: busArrivals)
                    
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
                if let busArrivals = busStopArrivalItemData.busStopArrival.busArrivals as? BusArrivals.DataNotAvailable {
                    BusServiceView(
                        busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber
                    )
                    .padding(.vertical, 4)
                    
                    BusArrivalErrorView(busArrivals: busArrivals)
                        .padding(.horizontal)
                        .padding(.vertical, 4)
                }
                
                if let busArrivals = busStopArrivalItemData.busStopArrival.busArrivals as? BusArrivals.NotOperating {
                    BusServiceView(
                        busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber
                    )
                    .padding(.vertical, 4)
                    
                    BusArrivalErrorView(busArrivals: busArrivals)
                        .padding(.horizontal)
                        .padding(.vertical, 4)
                }
                
                if let busArrivals = busStopArrivalItemData.busStopArrival.busArrivals as? BusArrivals.Error {
                    BusServiceView(
                        busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber
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

struct BusStopArrivalItemView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            BusStopArrivalItemView(
                busStopArrivalItemData: BusStopArrivalItemData(
                    busStopArrival: BusStopArrival(
                        busStopCode: "123456",
                        busServiceNumber: "961M",
                        operator: "SMRT",
                        direction: 1,
                        stopSequence: 24,
                        distance: 14.6,
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
                                        busStopDescription: "Destination Bus Stop"
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
                )
            )
            .padding()
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.light)
            
            BusStopArrivalItemView(
                busStopArrivalItemData: BusStopArrivalItemData(
                    busStopArrival: BusStopArrival(
                        busStopCode: "123456",
                        busServiceNumber: "961M",
                        operator: "SMRT",
                        direction: 1,
                        stopSequence: 24,
                        distance: 14.6,
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
                                        busStopDescription: "Destination Bus Stop"
                                    ),
                                    arrival: 6,
                                    latitude: 1.2,
                                    longitude: 1.2,
                                    visitNumber: 1,
                                    load: BusLoad.sea,
                                    wheelchairAccess: false,
                                    type: BusType.dd
                                ),
                            followingArrivingBusList: []
                        )
                    )
                )
            )
            .padding()
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
            
            BusStopArrivalItemView(
                busStopArrivalItemData: BusStopArrivalItemData(
                    busStopArrival: BusStopArrival(
                        busStopCode: "123456",
                        busServiceNumber: "961M",
                        operator: "SMRT",
                        direction: 1,
                        stopSequence: 24,
                        distance: 14.6,
                        busArrivals: BusArrivals.NotOperating.init()
                    )
                )
            )
            .padding()
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.light)
            
            BusStopArrivalItemView(
                busStopArrivalItemData: BusStopArrivalItemData(
                    busStopArrival: BusStopArrival(
                        busStopCode: "123456",
                        busServiceNumber: "961M",
                        operator: "SMRT",
                        direction: 1,
                        stopSequence: 24,
                        distance: 14.6,
                        busArrivals: BusArrivals.DataNotAvailable.init()
                    )
                )
            )
            .padding()
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
        }
    }
}
