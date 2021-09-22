//
//  SettingsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 22/09/21.
//

import SwiftUI

struct SettingsView: View {
    var body: some View {
        VStack(alignment: .center) {
            Image(systemName: "bus.fill")
                .resizable()
                .scaledToFit()
                .frame(width: 48, height: 48)
                .foregroundColor(.white)
                .padding(12)
                .background(Color.accentColor)
                .cornerRadius(20)
                //.padding(.top, 128)
            
            Text("Next Bus SG")
                .font(NxtBuzFonts.body)
                .fontWeight(.bold)
                //.padding(.top, 72)
                .padding(.horizontal)
                //.foregroundColor(.accentColor)
            
            if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                Text(version)
                    .font(NxtBuzFonts.caption)
                    .fontWeight(.medium)
                    //.padding(.top, 72)
                    .padding(.horizontal)
                    .foregroundColor(.secondary)
            }
        }
        .frame(
            maxWidth: .infinity,
            maxHeight: .infinity,
            alignment: .top
        )
        .padding()
    }
}

//struct SettingsView_Previews: PreviewProvider {
//    static var previews: some View {
//        SettingsView()
//    }
//}
