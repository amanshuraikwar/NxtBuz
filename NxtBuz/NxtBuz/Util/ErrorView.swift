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
    
    var body: some View {
        VStack(
            spacing: 0
        ) {
            Image(systemName: systemName)
                .resizable()
                .scaledToFit()
                .frame(width: 32, height: 32)
                .padding(8)
                .foregroundColor(Color.secondary)
                .background(Color(.systemGray4))
                .cornerRadius(8)
                .padding(.top)
            
            Text(errorMessage)
                .multilineTextAlignment(.center)
                .font(NxtBuzFonts.title3)
                .foregroundColor(.primary)
                .frame(maxWidth: .infinity)
                .padding()
                .fixedSize(horizontal: false, vertical: true)
            
            Divider()
            
            Text(retryText)
                .foregroundColor(.accentColor)
                .font(NxtBuzFonts.body)
                .fontWeight(.medium)
                .padding()
                .frame(maxWidth: .infinity)
                .onTapGesture {
                    onRetry()
                }
        }
        .background(Color(.systemGray6))
        .cornerRadius(8)
        .frame(maxWidth: .infinity)
        .padding()
    }
}
