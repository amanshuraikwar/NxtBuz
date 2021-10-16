//
//  WarningBannerView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 01/10/21.
//

import SwiftUI

struct WarningBannerView: View {
    let message: String
    let iconSystemName: String
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        HStack {
            Text(message)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
            
            Spacer()
            
            Image(systemName: iconSystemName)
                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
        }
    }
}

//struct WarningBannerView_Previews: PreviewProvider {
//    static var previews: some View {
//        WarningBannerView()
//    }
//}
