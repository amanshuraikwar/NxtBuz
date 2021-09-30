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
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        switch setupScreenState {
            case SetupScreenState.NotStarted:
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
                
                Text("Setting up...")
                    .font(NxtBuzFonts.body)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
                    .padding()
            case SetupScreenState.InProgress(let progress):
                Text("Setting up...(\(Int(progress * 100))%)")
                    .font(NxtBuzFonts.body)
                    .fontWeight(.medium)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
                    .padding(.horizontal)
                
                ProgressView(value: progress, total: 1.0)
                    .progressViewStyle(LinearProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
                    .padding()
            case SetupScreenState.Failed:
                Text("FAILED!")
                    .font(NxtBuzFonts.body)
                    .foregroundColor(Color(nxtBuzTheme.primaryColor))
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
