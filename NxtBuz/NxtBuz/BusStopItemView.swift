//
//  BusStopItemView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import SwiftUI

struct BusStopItemView: View {
    let busStopName: String
    let roadName: String
    let busStopCode: String
    let operatingBusServiceNumbers: String
    
    var body: some View {
        VStack {
            Text(busStopName)
                .font(NxtBuzFonts.title2)
                .fontWeight(.bold)
                .padding(.top, 4)
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
            
            Text(roadName.uppercased() + " • " + busStopCode)
                .font(NxtBuzFonts.caption)
                .padding(.top, 1)
                .foregroundColor(Color(.systemGray))
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
            
            Text(operatingBusServiceNumbers)
                .font(NxtBuzFonts.body)
                .fontWeight(.medium)
                .foregroundColor(.accentColor)
                .padding(.top, 2)
                .padding(.bottom, 4)
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
        }
        .frame(
            maxWidth: .infinity,
            alignment: .leading
        )
    }
}

struct BusStopItemView_Previews: PreviewProvider {
    static var previews: some View {
        BusStopItemView(
            busStopName: "Opp Blk 19",
            roadName: "Jln Jurong Kechil",
            busStopCode: "123456",
            operatingBusServiceNumbers: "961M  961 174 61 970 147"
        )
    }
}
