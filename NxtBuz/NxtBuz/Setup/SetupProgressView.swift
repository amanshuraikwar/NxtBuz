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
                PrimaryButton(
                    text: "Get Started",
                    action: { onSetupComplete() },
                    iconSystemName: "chevron.forward"
                )
                .padding()
        }
    }
}
