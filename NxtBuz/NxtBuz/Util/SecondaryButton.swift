//
//  SecondaryButton.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 01/10/21.
//

import SwiftUI

struct SecondaryButton: View {
    let text: String
    let onClick: () -> Void
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    @State private var pressed = false
    
    var body: some View {
        Text(text)
            .foregroundColor(Color(nxtBuzTheme.accentColor))
            .font(NxtBuzFonts.body)
            .fontWeight(.medium)
            .padding()
            .frame(maxWidth: .infinity)
            .background(pressed ? Color(nxtBuzTheme.accentColor).opacity(0.1)  : Color(.systemGray6).opacity(0.0))
            .onLongPressGesture(
                minimumDuration: .infinity,
                maximumDistance: .infinity,
                pressing: { pressing in
                    withAnimation(.easeInOut(duration: 0.3)) {
                        self.pressed = pressing
                    }
                    if !pressing {
                        onClick()
                    }
                },
                perform: {}
            )
    }
}

//struct SecondaryButton_Previews: PreviewProvider {
//    static var previews: some View {
//        SecondaryButton()
//    }
//}
