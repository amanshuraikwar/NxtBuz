//
//  GoingHomeBusView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 16/10/21.
//

import SwiftUI

struct GoingHomeBusView: View {
    let busServiceNumber: String
    let sourceBusStopDescription: String
    let destinationBusStopDescription: String
    let distance: Double
    let stops: Int
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack(
            alignment: .top
        ) {
            BusServiceNumberView(
                busServiceNumber: busServiceNumber,
                error: false
            )
            
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                Text(sourceBusStopDescription)
                    .font(NxtBuzFonts.footnote)
                    .fontWeight(.medium)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
                 
                HStack {
                    Image(systemName: "arrow.turn.down.right")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .foregroundColor(
                            Color(nxtBuzTheme.secondaryColor)
                        )
                    
                    Text(destinationBusStopDescription)
                        .font(NxtBuzFonts.footnote)
                        .fontWeight(.medium)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                }
                .padding(.top, 2)
                
                HStack {
                    Image(systemName: "bolt.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .foregroundColor(
                            Color(nxtBuzTheme.secondaryColor)
                        )
                    
                    Text("\(stops) STOPS  •  \(formatDistance(distance))")
                        .font(NxtBuzFonts.footnote)
                        .fontWeight(.medium)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                }
                .padding(.top, 6)
            }
            .padding(
                .leading, 8
            )
        }
        .padding(.vertical, 8)
    }
    
    func formatDistance(_ distance: Double) -> String {
        if distance < 1 {
            return "\(Int(distance * 1000)) M"
        } else {
            return "\(Int(distance)) KM"
        }
    }
}
//
//struct GoingHomeBusView_Previews: PreviewProvider {
//    static var previews: some View {
//        GoingHomeBusView()
//    }
//}
