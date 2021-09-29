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
                        .fontWeight(.medium)
                    
                    if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                        Text(version)
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.medium)
                            .foregroundColor(.accentColor)
                            .padding(4)
                            .background(Color(.systemGray5))
                            .cornerRadius(8)
                    }
                }
            }
            
//            Section(
//                header: Text("Starred")
//                    .font(NxtBuzFonts.caption)
//            ) {
//                Toggle(
//                    isOn: $showGreeting,
//                    label: {
//                        Text("Show starred buses that are not arriving")
//                            .font(NxtBuzFonts.body)
//                            .foregroundColor(.primary)
//                            .fontWeight(.bold)
//                    }
//                ).toggleStyle(SwitchToggleStyle(tint: .accentColor))
//
//            }
            
            Section {
                Button(
                    action: {
                        UIApplication.shared.open(
                            NSURL(string: "mailto:amanshuraikwar.dev@gmail.com")! as URL
                        )
                    }
                ) {
                    HStack {
                        Text("Request a Feature")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(.primary)
                            .fontWeight(.medium)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.forward")
                            .foregroundColor(.secondary)
                    }
                }
                
                Button(
                    action: {
                        UIApplication.shared.open(
                            NSURL(string: "https://amanshuraikwar.github.io")! as URL
                        )
                    }
                ) {
                    HStack {
                        Text("Made by Amanshu Raikwar")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(.primary)
                            .fontWeight(.medium)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.forward")
                            .foregroundColor(.secondary)
                    }
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
