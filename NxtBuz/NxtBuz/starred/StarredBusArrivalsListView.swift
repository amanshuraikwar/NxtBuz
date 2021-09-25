//
//  StarredBusArrivalsListView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import SwiftUI

struct StarredBusArrivalsListView: View {
    @StateObject var data: StarredBusArrivalsScreenSuccessData
    
    var body: some View {
        if data.shouldShowList {
            List {
                if data.outdatedResults {
                    HStack {
                        Image(systemName: "exclamationmark.icloud.fill")
                            .foregroundColor(Color.secondary)
                        
                        Text(
                            "Bus arrival times might be outdated."
                        )
                    }
                    .animation(.easeInOut, value: data.outdatedResults)
                }
                
                ForEach(data.starredBusStopList) { starredBusStop in
                    Section(
                        header: Text(starredBusStop.busStopDescription)
                            .font(NxtBuzFonts.body)
                    ) {
                        ForEach(starredBusStop.starredBusArrivalItemDataList) { starredBusArrivalItemData in
                            StarredBusArrivalsItemView(
                                starredBusArrivalItemData: starredBusArrivalItemData
                            )
                        }
                    }
                }
            }
            .listStyle(InsetGroupedListStyle())
        } else {
            VStack(
                spacing: 32
            ) {
                Image(systemName: "star.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 48, height: 48)
                    .foregroundColor(Color.secondary)
                
                Text("Your starred bus services will show up here :)")
                    .font(NxtBuzFonts.title)
                    .foregroundColor(.secondary)
                    .fontWeight(.bold)
                    .padding()
                    .multilineTextAlignment(.center)
            }
        }
    }
}

//struct StarredBusArrivalsListView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsListView()
//    }
//}
