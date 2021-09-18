//
//  SetupProgressView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import SwiftUI

struct SetupProgressView: View {
    @Binding var setupScreenState: SetupScreenState
    let onSetupComplete: () -> Void
    
    var body: some View {
        switch setupScreenState {
            case SetupScreenState.NotStarted:
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
                
                Text("Setting up...")
                    .font(NxtBuzFonts.body)
                    .padding()
            case SetupScreenState.InProgress(let progress):
                Text("Setting up...(\(Int(progress * 100))%)")
                    .font(NxtBuzFonts.body)
                    .fontWeight(.medium)
                    .padding(.horizontal)
                
                ProgressView(value: progress, total: 1.0)
                    .padding()
            case SetupScreenState.Failed:
                Text("FAILED!")
                    .font(NxtBuzFonts.body)
                    .padding()
            case SetupScreenState.Complete:
                Button(
                    action: {
                        onSetupComplete()
                    }
                ) {
                    Text("Get Started".uppercased())
                        .font(NxtBuzFonts.body)
                        .fontWeight(.medium)
                    Image(systemName: "chevron.forward")
                }
                .foregroundColor(.white)
                .frame(
                    maxWidth: .infinity,
                    alignment: .center
                )
                .padding()
                .background(Color.accentColor)
                .cornerRadius(16)
                .padding()
        }
    }
}
