//
//  PrimaryButton.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 23/09/21.
//

import SwiftUI

struct PrimaryButton: View {
    let text: String
    let action: () -> Void
    let iconSystemName: String?
    
    var body: some View {
        Button(action: action) {
            HStack {
                Text(text)
                    .font(NxtBuzFonts.body)
                    .fontWeight(.bold)
                
                if let iconSystemName = iconSystemName {
                    Image(systemName: iconSystemName)
                }
            }
            .foregroundColor(Color.white)
            .padding(14)
            .frame(
                maxWidth: .infinity,
                alignment: .center
            )
            .background(Color.accentColor)
            .cornerRadius(16)
        }
    }
}

//struct PrimaryButton_Previews: PreviewProvider {
//    static var previews: some View {
//        PrimaryButton()
//    }
//}
