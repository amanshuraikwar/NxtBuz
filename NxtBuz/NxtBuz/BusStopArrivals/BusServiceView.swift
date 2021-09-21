//
//  BusServiceView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI
import iosUmbrella

struct BusServiceView: View {
    let busServiceNumber: String
    let busType: BusType
    let busTypeSystemName: String
    let error: Bool
    
    init(busServiceNumber: String, busType: BusType) {
        self.busServiceNumber = busServiceNumber
        self.busType = busType
        if (busType == BusType.dd) {
            self.busTypeSystemName = "bus.doubledecker.fill"
        } else {
            self.busTypeSystemName = "bus.fill"
        }
        self.error = false
    }
    
    init(busServiceNumber: String) {
        self.busServiceNumber = busServiceNumber
        self.busType = BusType.sd
        self.busTypeSystemName = "bus.fill"
        self.error = true
    }
    
    var body: some View {
        HStack {
            if error {
                Image(systemName: busTypeSystemName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundColor(Color(.systemGray))
                
                ZStack {
                    Text(busServiceNumber)
                        .font(NxtBuzFonts.headline)
                        .foregroundColor(Color(.systemGray6))
                    
                    Text("961M")
                        .font(NxtBuzFonts.headline)
                        .opacity(0.0)
                }
                .padding(.vertical, 4)
                .padding(.horizontal, 8)
                .background(Color(.systemGray))
                .clipShape(Capsule())
            } else {
                Image(systemName: busTypeSystemName)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                
                ZStack {
                    Text(busServiceNumber)
                        .font(NxtBuzFonts.headline)
                        .foregroundColor(Color(.systemGray6))
                    
                    Text("961M")
                        .font(NxtBuzFonts.headline)
                        .opacity(0.0)
                }
                .padding(.vertical, 4)
                .padding(.horizontal, 8)
                .background(Color.accentColor)
                .clipShape(Capsule())
            }
        }
    }
}

struct BusServiceView_Previews: PreviewProvider {
    static var previews: some View {
        BusServiceView(busServiceNumber: "961M", busType: BusType.dd)
    }
}
