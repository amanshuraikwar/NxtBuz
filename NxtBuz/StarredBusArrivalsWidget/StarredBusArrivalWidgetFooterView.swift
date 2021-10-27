//
//  StarredBusArrivalWidgetFooterView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 26/10/21.
//

import SwiftUI

struct StarredBusArrivalWidgetFooterView: View {
    let date: Date
    
    @EnvironmentObject private var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack(
            alignment: .bottom
        ) {
            Text("\(getTime(date: date))")
                .font(NxtBuzFonts.caption)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                .frame(maxWidth: .infinity, alignment: .leading)
                
            Spacer()
            
            Text("STARRED BUSES")
                .font(NxtBuzFonts.caption)
                .fontWeight(.bold)
                .foregroundColor(.yellow)
        }
    }
    
    public func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}

//struct StarredBusArrivalWidgetFooterView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalWidgetFooterView()
//    }
//}
