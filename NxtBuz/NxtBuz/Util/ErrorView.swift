//
//  ErrorView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/09/21.
//

import SwiftUI

struct ErrorView: View {
    let systemName: String
    let errorMessage: String
    let retryText: String
    let onRetry: () -> Void
    let iconSystemName: String?
    
    var body: some View {
        VStack(
            spacing: 32
        ) {
            Image(systemName: systemName)
                .resizable()
                .scaledToFit()
                .frame(width: 48, height: 48)
                .foregroundColor(Color.accentColor)
            
            Text(errorMessage)
                .font(NxtBuzFonts.title3)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
                .padding(.horizontal)
            
            PrimaryButton(
                text: retryText,
                action: onRetry,
                iconSystemName: iconSystemName
            ).padding(.horizontal)
        }
    }
}
