//
//  StarredBusArrivalsItemView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 23/09/21.
//

import SwiftUI
import iosUmbrella

struct StarredBusArrivalsItemView: View {
    let error: Bool
    var body: some View {
        VStack(
            spacing: 6
        ) {
            Text("Opp Blk 19")
                .font(NxtBuzFonts.callout)
                .foregroundColor(.primary)
            
            HStack(spacing: 4) {
                if error {
                    Image("BusTypeDd")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.secondary)
                    
                    ZStack {
                        Text("961M")
                            .font(NxtBuzFonts.callout)
                            .foregroundColor(Color(.systemGray6))
                        
                        Text("961M")
                            .font(NxtBuzFonts.callout)
                            .opacity(0.0)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .background(Color(.systemGray))
                    .clipShape(Capsule())
                    
                    Image(systemName: "chevron.right")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .foregroundColor(Color.primary)
                    
                    Text("N/A")
                        .font(NxtBuzFonts.callout)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                } else {
                    Image("BusTypeDd")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.primary)
                    
                    ZStack {
                        Text("961M")
                            .font(NxtBuzFonts.callout)
                            .foregroundColor(Color(.systemGray6))
                        
                        Text("961M")
                            .font(NxtBuzFonts.callout)
                            .opacity(0.0)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .background(Color.accentColor)
                    .clipShape(Capsule())
                    
                    Image(systemName: "chevron.right")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 12, height: 12)
                        .foregroundColor(Color.primary)
                    
                    Text("04")
                        .font(NxtBuzFonts.callout)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                }
            }
        }
        .padding(8)
        .background(Color(.systemGray6))
        .cornerRadius(16)
    }
}

//struct StarredBusArrivalsItemView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsItemView()
//    }
//}
