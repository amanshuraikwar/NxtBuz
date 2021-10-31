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
    
    let onShowMoreClick: () -> ()
    let onShowLessClick: () -> ()
    
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
        case .Success(let result, let showMore, let showLess):
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
                    showMore ? Array(result.directBuses[0...0].enumerated()) : Array(result.directBuses.enumerated()),
                    id: \.1
                ) { index, directBus in
                    NavigationLink(
                        destination: BusStopArrivalsView(
                            busStopCode: directBus.sourceBusStopCode
                        )
                    ) {
                        GoingHomeBusView(
                            busServiceNumber: directBus.busServiceNumber,
                            sourceBusStopDescription: directBus.sourceBusStopDescription,
                            destinationBusStopDescription: directBus.destinationBusStopDescription,
                            distance: directBus.distance,
                            stops: Int(directBus.stops)
                        )
                    }
                }
                
                if showMore || showLess {
                    HStack {
                        Spacer()
                        
                        Text(showMore ? "Show More" : "Show Less")
                            .font(NxtBuzFonts.footnote)
                        
                        Image(systemName: "chevron.right")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 12, height: 12)
                            .rotationEffect(.degrees(showLess ? -90 : +90))
                            .padding(.leading, 2)    
                    }
                    .foregroundColor(Color(nxtBuzTheme.accentColor))
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .cornerRadius(8)
                    .onTapGesture {
                        showMore ? onShowMoreClick() : onShowLessClick()
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
