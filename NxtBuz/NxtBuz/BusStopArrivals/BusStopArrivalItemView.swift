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
    var onStarToggle: (_ newValue: Bool) -> Void
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    @State private var expanded = false
    
    var body: some View {
        VStack(
            alignment: .leading,
            spacing: 0
        ) {
            if let arriving = busStopArrivalItemData.busStopArrival.busArrivals as? BusArrivals.Arriving {
                HStack {
                    BusServiceNumberView(
                        busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber,
                        error: false
                    )
                    
                    DestinationBusStopView(
                        busStopDescription: arriving.nextArrivingBus.destination.busStopDescription
                    )
                    .padding(.leading, 8)
                    
                    Spacer()
                    
                    Image(systemName: busStopArrivalItemData.starred ? "star.fill" : "star")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20, height: 20)
                        .foregroundColor(Color.yellow)
                        .padding(4)
                        .background(Color.yellow.opacity(0.1))
                        .cornerRadius(4)
                        .onTapGesture {
                            onStarToggle(!busStopArrivalItemData.starred)
                        }
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
                                .foregroundColor(
                                    Color(nxtBuzTheme.secondaryColor)
                                )
                                .padding(6)
                        }
                        
                        ArrivingBusView(
                            arrivingBus: arriving.nextArrivingBus
                        ).padding(.leading, 8)
                        
                        Spacer()
                        
                        if !arriving.followingArrivingBusList.isEmpty {
                            Image(systemName: "chevron.right.circle.fill")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 20, height: 20)
                                .foregroundColor(Color(nxtBuzTheme.accentColor))
                                .rotationEffect(.degrees(expanded ? -90 : +90))
                                .padding(4)
                                .background(Color(nxtBuzTheme.accentColor).opacity(0.1))
                                .cornerRadius(4)
                                .onTapGesture {
                                    expanded.toggle()
                                }
                        }
                    }
                    .padding(.top, 16)
                    
                    if expanded {
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
                                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                                        .padding(6)
                                }
                                
                                ArrivingBusView(
                                    arrivingBus: arrivingBus
                                ).padding(.leading, 8)
                                
                                Spacer()
                            }
                            .padding(.top, 12)
                        }
                    }
                }
                .padding(.bottom, 12)
            } else {
                HStack {
                    BusServiceNumberView(
                        busServiceNumber: busStopArrivalItemData.busStopArrival.busServiceNumber,
                        error: true
                    )
                    
                    if let _ = busStopArrivalItemData.busStopArrival.busArrivals as? BusArrivals.NotOperating {
                        Text("Not Operating")
                            .font(NxtBuzFonts.title2)
                            .fontWeight(.bold)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                            .padding(.leading, 8)
                    } else {
                        Text("No Data")
                            .font(NxtBuzFonts.title2)
                            .fontWeight(.bold)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                            .padding(.leading, 8)
                    }
                    
                    Spacer()
                    
                    Button(
                        action: {
                            onStarToggle(!busStopArrivalItemData.starred)
                        }
                    ) {
                        if busStopArrivalItemData.starred {
                            Image(systemName: "star.fill")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 20, height: 20)
                                .foregroundColor(Color.yellow)
                                .frame(maxHeight: .infinity, alignment: .center)
                        } else {
                            Image(systemName: "star")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 20, height: 20)
                                .foregroundColor(Color.yellow)
                                .frame(maxHeight: .infinity, alignment: .center)
                        }
                    }
                }
                .padding(.vertical, 12)
            }
        }
    }
}

//struct BusStopArrivalItemView_Previews: PreviewProvider {
//    static var previews: some View {
//        Group {
//            BusStopArrivalItemView(
//                busStopArrivalItemData: BusStopArrivalItemData(
//                    busStopArrival: BusStopArrival(
//                        busStopCode: "123456",
//                        busServiceNumber: "961M",
//                        operator: "SMRT",
//                        direction: 1,
//                        stopSequence: 24,
//                        distance: 14.6,
//                        busArrivals: BusArrivals.Arriving.init(
//                            nextArrivingBus:
//                                ArrivingBus(
//                                    origin: ArrivingBusStop(
//                                        busStopCode: "123456",
//                                        roadName: "This Road",
//                                        busStopDescription: "Origin Bus Stop"
//                                    ),
//                                    destination: ArrivingBusStop(
//                                        busStopCode: "123456",
//                                        roadName: "This Road",
//                                        busStopDescription: "Destination Bus Stop"
//                                    ),
//                                    arrival: 6,
//                                    latitude: 1.2,
//                                    longitude: 1.2,
//                                    visitNumber: 1,
//                                    load: BusLoad.lsd,
//                                    wheelchairAccess: true,
//                                    type: BusType.bd
//                                ),
//                            followingArrivingBusList: []
//                        )
//                    )
//                )
//            )
//            .padding()
//            .previewLayout(.sizeThatFits)
//            .preferredColorScheme(.light)
//            
//            BusStopArrivalItemView(
//                busStopArrivalItemData: BusStopArrivalItemData(
//                    busStopArrival: BusStopArrival(
//                        busStopCode: "123456",
//                        busServiceNumber: "961M",
//                        operator: "SMRT",
//                        direction: 1,
//                        stopSequence: 24,
//                        distance: 14.6,
//                        busArrivals: BusArrivals.Arriving.init(
//                            nextArrivingBus:
//                                ArrivingBus(
//                                    origin: ArrivingBusStop(
//                                        busStopCode: "123456",
//                                        roadName: "This Road",
//                                        busStopDescription: "Origin Bus Stop"
//                                    ),
//                                    destination: ArrivingBusStop(
//                                        busStopCode: "123456",
//                                        roadName: "This Road",
//                                        busStopDescription: "Destination Bus Stop"
//                                    ),
//                                    arrival: 6,
//                                    latitude: 1.2,
//                                    longitude: 1.2,
//                                    visitNumber: 1,
//                                    load: BusLoad.sea,
//                                    wheelchairAccess: false,
//                                    type: BusType.dd
//                                ),
//                            followingArrivingBusList: []
//                        )
//                    )
//                )
//            )
//            .padding()
//            .previewLayout(.sizeThatFits)
//            .preferredColorScheme(.dark)
//            
//            BusStopArrivalItemView(
//                busStopArrivalItemData: BusStopArrivalItemData(
//                    busStopArrival: BusStopArrival(
//                        busStopCode: "123456",
//                        busServiceNumber: "961M",
//                        operator: "SMRT",
//                        direction: 1,
//                        stopSequence: 24,
//                        distance: 14.6,
//                        busArrivals: BusArrivals.NotOperating.init()
//                    )
//                )
//            )
//            .padding()
//            .previewLayout(.sizeThatFits)
//            .preferredColorScheme(.light)
//            
//            BusStopArrivalItemView(
//                busStopArrivalItemData: BusStopArrivalItemData(
//                    busStopArrival: BusStopArrival(
//                        busStopCode: "123456",
//                        busServiceNumber: "961M",
//                        operator: "SMRT",
//                        direction: 1,
//                        stopSequence: 24,
//                        distance: 14.6,
//                        busArrivals: BusArrivals.DataNotAvailable.init()
//                    )
//                )
//            )
//            .padding()
//            .previewLayout(.sizeThatFits)
//            .preferredColorScheme(.dark)
//        }
//    }
//}
