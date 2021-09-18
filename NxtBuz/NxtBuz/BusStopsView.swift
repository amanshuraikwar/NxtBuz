//
//  BusStopsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import SwiftUI
import iosUmbrella

struct BusStopsView: View {
    @Binding var busStopList: [BusStop]
    
    var body: some View {
        List {
            Section(
                header: Text("Nearby Bus Stops")
                    .font(NxtBuzFonts.caption)
            ) {
                ForEach(Array(busStopList.enumerated()), id: \.1) { index, busStop in
                    BusStopItemView(
                        busStopName: busStop.description_,
                        roadName: busStop.roadName,
                        busStopCode: busStop.code,
                        operatingBusServiceNumbers: getOperatingBusStr(busStop.operatingBusList)
                    )
                }
            }
        }
        .id(UUID())
    }
    
    func getOperatingBusStr(_ operatingBusList: [Bus]) -> String {
        var str = ""
        for index in 0...operatingBusList.count-1 {
            str += operatingBusList[index].serviceNumber
            if (index != operatingBusList.count-1) {
                str += "  "
            }
        }
        return str
    }
}
