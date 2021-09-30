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
    let busTypeName: String
    let error: Bool
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    init(busServiceNumber: String, busType: BusType) {
        self.busServiceNumber = busServiceNumber
        self.busType = busType
        if (busType == BusType.dd) {
            self.busTypeName = "BusTypeDd"
        } else if (busType == BusType.bd) {
            self.busTypeName = "BusTypeFeeder"
        } else {
            self.busTypeName = "BusTypeNormal"
        }
        self.error = false
    }
    
    init(busServiceNumber: String) {
        self.busServiceNumber = busServiceNumber
        self.busType = BusType.sd
        self.busTypeName = "BusTypeNormal"
        self.error = true
    }
    
    var body: some View {
        HStack {
            if error {
                Image(busTypeName)
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 20)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                
                BusServiceNumberView(
                    busServiceNumber: busServiceNumber,
                    error: true
                )
            } else {
                Image(busTypeName)
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 20, height: 20)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
                
                BusServiceNumberView(
                    busServiceNumber: busServiceNumber,
                    error: false
                )
            }
        }
    }
}

struct BusServiceView_Previews: PreviewProvider {
    static var previews: some View {
        BusServiceView(busServiceNumber: "961M", busType: BusType.dd)
    }
}
