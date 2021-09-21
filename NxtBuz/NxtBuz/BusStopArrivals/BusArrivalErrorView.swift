//
//  BusArrivalErrorView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI
import iosUmbrella

struct BusArrivalErrorView: View {
    let errorStr: String
    
    init(busArrivals: BusArrivals.DataNotAvailable) {
        self.errorStr = "No Data"
    }
    
    init(busArrivals: BusArrivals.NotOperating) {
        self.errorStr = "Not Operating"
    }
    
    init(busArrivals: BusArrivals.Error) {
        self.errorStr = "No Data"
    }
    
    var body: some View {
        Text(errorStr)
            .font(NxtBuzFonts.title2)
            .fontWeight(.bold)
    }
}
