//
//  DestinationBusStopView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 27/09/21.
//

import SwiftUI

struct DestinationBusStopView: View {
    let busStopDescription: String
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack(
            spacing: 0
        ) {
            Image(systemName: "arrow.right.circle.fill")
                .resizable()
                .scaledToFit()
                .frame(width: 16, height: 16)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                .padding(6)
            
            Text(busStopDescription)
                .font(NxtBuzFonts.callout)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                .padding(.trailing, 6)
        }
        .background(Color(.systemGray5))
        .cornerRadius(8)
    }
}

//struct DestinationBusStopView_Previews: PreviewProvider {
//    static var previews: some View {
//        DestinationBusStopView()
//    }
//}
