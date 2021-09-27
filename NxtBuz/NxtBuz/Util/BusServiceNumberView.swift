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
    
    var body: some View {
        ZStack {
            Text(busServiceNumber)
                .font(NxtBuzFonts.title3)
                .fontWeight(.bold)
                .foregroundColor(error ? Color(.systemGray5) : Color.white)
            
            Text("961M ")
                .font(NxtBuzFonts.title3)
                .fontWeight(.bold)
                .opacity(0.0)
        }
        .padding(.vertical, 2)
        .padding(.horizontal, 4)
        .background(error ? Color(.systemGray) : Color.accentColor)
        .clipShape(Capsule())
    }
}

//struct StarredBusServiceView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusServiceView()
//    }
//}
