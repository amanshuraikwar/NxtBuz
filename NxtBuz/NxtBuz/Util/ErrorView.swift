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
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        VStack(
            spacing: 0
        ) {
            Image(systemName: systemName)
                .resizable()
                .scaledToFit()
                .frame(width: 32, height: 32)
                .padding(8)
                .foregroundColor(Color(nxtBuzTheme.accentColor))
                .background(Color(nxtBuzTheme.accentColor).opacity(0.1))
                .cornerRadius(8)
                .padding(.top)
            
            Text(errorMessage)
                .multilineTextAlignment(.center)
                .font(NxtBuzFonts.title3)
                .foregroundColor(Color(nxtBuzTheme.primaryColor))
                .frame(maxWidth: .infinity)
                .padding()
                .fixedSize(horizontal: false, vertical: true)
            
            Divider()
            
            SecondaryButton(
                text: retryText,
                onClick: onRetry
            )
        }
        .background(Color(.systemGray6))
        .cornerRadius(8)
        .frame(maxWidth: .infinity)
        .padding()
    }
}
