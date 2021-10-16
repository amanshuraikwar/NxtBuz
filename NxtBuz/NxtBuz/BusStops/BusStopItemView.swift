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
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        VStack(
            spacing: 0
        ) {
            Text(busStopName)
                .font(NxtBuzFonts.headline)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
                .fontWeight(.regular)
                .padding(.top, 4)
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
            
            Text((roadName + "  •  " + busStopCode).uppercased())
                .font(NxtBuzFonts.caption)
                .padding(.top, 4)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
            
            Text(operatingBusServiceNumbers)
                .font(NxtBuzFonts.title3)
                .fontWeight(.bold)
                .foregroundColor(Color(nxtBuzTheme.accentColor))
                .padding(.trailing)
                .frame(
                    maxWidth: .infinity,
                    alignment: .leading
                )
                .padding(.vertical, 8)
                
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
