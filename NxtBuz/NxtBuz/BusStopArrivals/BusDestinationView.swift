//
//  BusDestinationView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 21/09/21.
//

import SwiftUI

struct BusDestinationView: View {
    let destination: String
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack {
            Image(systemName: "arrow.forward.circle.fill")
                .resizable()
                .scaledToFit()
                .frame(width: 12, height: 12)
            
            Text(destination)
                .font(NxtBuzFonts.footnote)
        }
        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
    }
}
