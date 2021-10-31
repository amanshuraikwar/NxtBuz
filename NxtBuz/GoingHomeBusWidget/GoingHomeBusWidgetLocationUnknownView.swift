//
//  GoingHomeBusWidgetLocationUnknownView.swift
//  GoingHomeBusWidgetExtension
//
//  Created by amanshu raikwar on 31/10/21.
//

import SwiftUI

struct GoingHomeBusWidgetLocationUnknownView: View {
    @EnvironmentObject private var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        VStack(
            alignment: .leading,
            spacing: 0
        ) {
            Image(systemName: "location.slash.fill")
                .resizable()
                .scaledToFit()
                .frame(width: 32, height: 32)
                .padding(8)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                .background(Color(nxtBuzTheme.secondaryColor).opacity(0.1))
                .cornerRadius(8)
                .padding(.top)
            
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

//struct GoingHomeBusWidgetLocationUnknownView_Previews: PreviewProvider {
//    static var previews: some View {
//        GoingHomeBusWidgetLocationUnknownView()
//    }
//}
