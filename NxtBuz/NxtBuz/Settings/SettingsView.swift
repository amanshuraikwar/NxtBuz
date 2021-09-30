//
//  SettingsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 22/09/21.
//

import SwiftUI

struct SettingsView: View {
    @State private var showGreeting = true
    
    @EnvironmentObject var nxtBuzTheme: NxtBuzTheme
    
    var body: some View {
        List {
            Section(
                header: Text("App Info")
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            ) {
                HStack {
                    Text("Next Bus SG")
                        .font(NxtBuzFonts.body)
                        .fontWeight(.medium)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                    
                    if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                        Text(version)
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.medium)
                            .foregroundColor(Color(nxtBuzTheme.accentColor))
                            .padding(4)
                            .background(Color(.systemGray5))
                            .cornerRadius(8)
                    }
                }
            }
            
            Section(
                header: Text("Dark Mode")
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            ) {
                Toggle(
                    isOn: $showGreeting,
                    label: {
                        Text("Dark mode")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                            .fontWeight(.medium)
                    }
                ).toggleStyle(SwitchToggleStyle(tint: Color(nxtBuzTheme.accentColor)))

                Toggle(
                    isOn: $showGreeting,
                    label: {
                        Text("Use device settings")
                            .font(NxtBuzFonts.body)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                            .fontWeight(.medium)
                    }
                ).toggleStyle(SwitchToggleStyle(tint: Color(nxtBuzTheme.accentColor)))
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
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                            .fontWeight(.medium)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.forward")
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
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
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                            .fontWeight(.medium)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.forward")
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
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
