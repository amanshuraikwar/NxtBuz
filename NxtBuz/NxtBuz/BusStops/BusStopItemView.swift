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
        VStack(
            spacing: 0
        ) {
            Text(busStopName)
                .font(NxtBuzFonts.headline)
                .fontWeight(.medium)
                .padding(.top, 4)
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
            
            Text(roadName + " • " + busStopCode)
                .font(NxtBuzFonts.caption)
                .padding(.top, 2)
                .foregroundColor(Color.secondary)
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
            
            Text(operatingBusServiceNumbers)
                .font(NxtBuzFonts.body)
                .fontWeight(.bold)
                .foregroundColor(.accentColor)
                .padding(.top, 4)
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
        Group {
            BusStopItemView(
                busStopName: "Opp Blk 19",
                roadName: "Jln Jurong Kechil",
                busStopCode: "123456",
                operatingBusServiceNumbers: "961M  961 174 61 970 147"
            )
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.light)
            .padding()
            BusStopItemView(
                busStopName: "Opp Blk 19",
                roadName: "Jln Jurong Kechil",
                busStopCode: "123456",
                operatingBusServiceNumbers: "961M  961  174  61  970  147  157  170  170A  184  41  52 66 67"
            )
            .previewLayout(.sizeThatFits)
            .preferredColorScheme(.dark)
            .padding()
        }
    }
}
