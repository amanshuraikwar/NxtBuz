//
//  SetupView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 18/09/21.
//

import SwiftUI
import iosUmbrella

struct SetupView: View {
    @StateObject private var viewModel = SetupViewModel()
    let onSetupComplete: () -> Void
    
    var body: some View {
        VStack {
            Image("Bus60")
                .resizable()
                .scaledToFit()
                .frame(width: 60, height: 60)
                .cornerRadius(16)
                .padding(.top, 128)
            
            Text("Next Bus SG")
                .font(NxtBuzFonts.title)
                .fontWeight(.bold)
                .padding(.top, 32)
                .padding(.horizontal)
                .foregroundColor(.accentColor)
            
            Text("Easily find bus arrival timings anywhere in Singapore")
                .font(NxtBuzFonts.headline)
                .fontWeight(.regular)
                .foregroundColor(Color(.systemGray))
                .padding(.top, 2)
                .padding(.horizontal)
                .multilineTextAlignment(.center)
                .frame(maxWidth: .infinity, alignment: .center)
            
            Spacer()
         
            SetupProgressView(
                setupScreenState: $viewModel.setupScreenState,
                onSetupComplete: onSetupComplete
            )
        }
        .frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .center
        )
        .onAppear {
            viewModel.startSetup()
        }
    }
}
