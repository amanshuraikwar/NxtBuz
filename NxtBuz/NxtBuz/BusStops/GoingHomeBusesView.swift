//
//  GoingHomeBusesView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 19/10/21.
//

import SwiftUI
import iosUmbrella

struct GoingHomeBusesView: View {
    @Binding var state: BusesGoingHomeState
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        switch state {
        case .Fetching:
            HStack {
                Text("Computing...")
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                    .font(NxtBuzFonts.body)
                
                Spacer()
                
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
            }
        case .Success(let result):
            if result is GoingHomeBusResult.NoBusStopsNearby {
                WarningBannerView(
                    message: "No bus stops nearby.",
                    iconSystemName: "sun.haze.fill"
                )
            }
            
            if result is GoingHomeBusResult.HomeBusStopNotSet {
                WarningBannerView(
                    message: "Home bus stop not set.",
                    iconSystemName: "sun.haze.fill"
                )
            }
            
            if let result = result as? GoingHomeBusResult.NoBusesGoingHome {
                WarningBannerView(
                    message: "No direct buses going to \(result.homeBusStopDescription).",
                    iconSystemName: "sun.haze.fill"
                )
            }
            
            if let result = result as? GoingHomeBusResult.TooCloseToHome {
                WarningBannerView(
                    message: "You are already near \(result.homeBusStopDescription).",
                    iconSystemName: "sun.haze.fill"
                )
            }
            
            if let result = result as? GoingHomeBusResult.Processing {
                HStack {
                    Text(result.message)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                    
                    Spacer()
                    
                    ProgressBar(progress: Float(result.progress)/100.0)
                        .frame(width: 24.0, height: 24.0)
                }
            }
            
            if let result = result as? GoingHomeBusResult.Success {
                ForEach(
                    Array(result.goingHomeBuses.enumerated()),
                    id: \.1
                ) { index, goingHomeBus in
                    NavigationLink(
                        destination: BusStopArrivalsView(
                            busStopCode: goingHomeBus.sourceBusStopCode
                        )
                    ) {
                        GoingHomeBusView(
                            busServiceNumber: goingHomeBus.busServiceNumber,
                            sourceBusStopDescription: goingHomeBus.sourceBusStopDescription,
                            destinationBusStopDescription: goingHomeBus.destinationBusStopDescription,
                            distance: goingHomeBus.distance,
                            stops: Int(goingHomeBus.stops)
                        )
                    }
                }
            }
        }
    }
}

//struct GoingHomeBusesView_Previews: PreviewProvider {
//    static var previews: some View {
//        GoingHomeBusesView()
//    }
//}
