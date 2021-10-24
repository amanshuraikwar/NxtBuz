//
//  StarredBusServiceView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 27/09/21.
//

import SwiftUI

struct BusServiceNumberView: View {
    let busServiceNumber: String
    let error: Bool
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        ZStack {
            Text(busServiceNumber)
                .font(NxtBuzFonts.title3)
                .fontWeight(.medium)
                .foregroundColor(error ? Color(.systemGray5) : nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
            
            Text("961M")
                .font(NxtBuzFonts.title3)
                .fontWeight(.medium)
                .opacity(0.0)
        }
        .padding(.vertical, 2)
        .padding(.horizontal, 8)
        .background(error ? Color(.systemGray) : Color(nxtBuzTheme.accentColor))
        .clipShape(Capsule())
    }
}

//struct StarredBusServiceView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusServiceView()
//    }
//}
