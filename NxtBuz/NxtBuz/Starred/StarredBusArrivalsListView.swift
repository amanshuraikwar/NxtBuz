//
//  StarredBusArrivalsListView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import SwiftUI

struct StarredBusArrivalsListView: View {
    @StateObject var data: StarredBusArrivalsScreenSuccessData
    let onUnStarClick: (_ busStopCode: String, _ busServiceNumber: String) -> Void
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        if data.shouldShowList {
            List {
                if data.outdatedResults {
                    HStack {
                        Text("Bus arrival times might be outdated.")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        
                        Image(systemName: "exclamationmark.icloud.fill")
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                    }
                }
                
                ForEach(data.starredBusStopList) { starredBusStop in
                    StarredBusStopView(
                        starredBusStop: starredBusStop,
                        onUnStarClick: onUnStarClick
                    )
                }
                
                Text("Last updated on \(data.lastUpdatedOnStr)")
                    .font(NxtBuzFonts.body)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            }
            .listStyle(InsetGroupedListStyle())
        } else {
            VStack {
                Image(systemName: "star.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 32, height: 32)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                
                Text("Your starred bus services will show up here :)")
                    .font(NxtBuzFonts.title3)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
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
