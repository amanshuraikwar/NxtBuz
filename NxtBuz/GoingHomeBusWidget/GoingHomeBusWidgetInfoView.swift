//
//  GoingHomeBusWidgetInfoView.swift
//  GoingHomeBusWidgetExtension
//
//  Created by amanshu raikwar on 26/10/21.
//

import SwiftUI

struct GoingHomeBusWidgetInfoView: View {
    let message: String
    
    @EnvironmentObject private var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        VStack(
            alignment: .leading,
            spacing: 0
        ) {
            Text(message)
                .font(NxtBuzFonts.body)
                .fontWeight(.bold)
                .multilineTextAlignment(.leading)
                .frame(maxWidth: .infinity, alignment: .leading)
                .fixedSize(horizontal: false, vertical: true)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                .lineLimit(4)
            
            Spacer()
            
            HStack(
                alignment: .bottom
            ) {
                Text("GOING HOME")
                    .font(NxtBuzFonts.caption)
                    .fontWeight(.bold)
                    .foregroundColor(Color(nxtBuzTheme.accentColor))
                
                Spacer()
            }
        }
    }
}

//struct GoingHomeBusWidgetInfoView_Previews: PreviewProvider {
//    static var previews: some View {
//        GoingHomeBusWidgetInfoView()
//    }
//}
