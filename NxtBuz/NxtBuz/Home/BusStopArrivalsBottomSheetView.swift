//
//  BusStopArrivalsBottomSheetView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 07/10/21.
//

import SwiftUI

struct BusStopArrivalsBottomSheetView: View {
    let busStopCode: String
    
    var body: some View {
        BusStopArrivalsView(busStopCode: busStopCode)
//            .onChange(of: busStopArrivalsSheetData.busStopCode) {
//                print("yoyoyoyoyoyoyoyo")
//            }
    }
}

//struct BusStopArrivalsBottomSheetView_Previews: PreviewProvider {
//    static var previews: some View {
//        BusStopArrivalsBottomSheetView()
//    }
//}
