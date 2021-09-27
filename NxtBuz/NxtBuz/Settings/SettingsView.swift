//
//  SettingsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 22/09/21.
//

import SwiftUI

struct SettingsView: View {
    @State private var showGreeting = true
    
    var body: some View {
        List {
            Section(
                header: Text("App Info")
                    .font(NxtBuzFonts.caption)
            ) {
                HStack {
                    Text("Next Bus SG")
                        .font(NxtBuzFonts.body)
                        .fontWeight(.bold)
                    
                    if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                        Text(version)
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.bold)
                            .foregroundColor(.accentColor)
                            .padding(4)
                            .background(Color(.systemGray5))
                            .cornerRadius(8)
                    }
                }
            }
            
            Section(
                header: Text("Starred")
                    .font(NxtBuzFonts.caption)
            ) {
                Toggle(
                    isOn: $showGreeting,
                    label: {
                        Text("Show starred buses that are not arriving")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(.primary)
                            .fontWeight(.bold)
                    }
                ).toggleStyle(SwitchToggleStyle(tint: .accentColor))
                
            }
            
            Section {
                Button(
                    action: {
                        
                    }
                ) {
                    Text("Request a Feature")
                        .font(NxtBuzFonts.body)
                        .foregroundColor(.primary)
                        .fontWeight(.bold)
                }
                
                Button(
                    action: {
                        
                    }
                ) {
                    Text("Made by Amanshu Raikwar")
                        .font(NxtBuzFonts.body)
                        .foregroundColor(.primary)
                        .fontWeight(.bold)
                }
            }
        }
        .listStyle(InsetGroupedListStyle())
    }
}

//struct SettingsView_Previews: PreviewProvider {
//    static var previews: some View {
//        SettingsView()
//    }
//}
