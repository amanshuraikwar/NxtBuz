//
//  BusArrivalView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 01/10/21.
//

import SwiftUI
import iosUmbrella

struct BusStopArrivalView: View {
    @Binding var busArrivals: BusArrivals
    let starFeatureEnabled: Bool
    @Binding var isStarred: Bool
    let busServiceNumber: String
    let desinationBusStopDescription: String
    let onStarToggle: (Bool) -> Void
    
    @State private var expanded = false
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        VStack(
            alignment: .leading,
            spacing: 0
        ) {
            if let arriving = busArrivals as? BusArrivals.Arriving {
                HStack {
                    BusServiceNumberView(
                        busServiceNumber: busServiceNumber,
                        error: false
                    )
                    
                    DestinationBusStopView(
                        busStopDescription: arriving.nextArrivingBus.destination.busStopDescription
                    )
                    .padding(.leading, 8)
                    
                    Spacer()
                    
                    Image(systemName: isStarred ? "star.fill" : "star")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 20, height: 20)
                        .foregroundColor(Color.yellow)
                        .padding(4)
                        .background(Color.yellow.opacity(0.1))
                        .cornerRadius(4)
                        .onTapGesture {
                            onStarToggle(!isStarred)
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
                                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
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
                                .foregroundColor(Color(nxtBuzTheme.primaryColor))
                                .rotationEffect(.degrees(expanded ? 90 : 0))
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
                        busServiceNumber: busServiceNumber,
                        error: true
                    )
                    
                    if let _ = busArrivals as? BusArrivals.NotOperating {
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
                            onStarToggle(!isStarred)
                        }
                    ) {
                        if isStarred {
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
//
//struct BusArrivalView_Previews: PreviewProvider {
//    static var previews: some View {
//        BusArrivalView()
//    }
//}
