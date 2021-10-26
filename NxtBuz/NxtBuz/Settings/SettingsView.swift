//
//  SettingsView.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 22/09/21.
//

import SwiftUI

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    
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
                    
                    Spacer()
                    
                    if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                        Text(version)
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.black)
                            .foregroundColor(Color(nxtBuzTheme.accentColor))
                            .padding(4)
                            .background(Color(nxtBuzTheme.accentColor).opacity(0.1))
                            .cornerRadius(8)
                    }
                }
            }
            
            Section(
                header: Text("Home Bus Stop")
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            ) {
                switch viewModel.homeStopState {
                case .Fetching:
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: Color(nxtBuzTheme.accentColor)))
                case .NoBusStop:
                    Text("No home bus stop set.")
                        .font(NxtBuzFonts.body)
                        .fontWeight(.medium)
                        .foregroundColor(Color(nxtBuzTheme.primaryColor))
                case .Success(let desc, let roadName, let busStopCode):
                    VStack(
                        alignment: .leading,
                        spacing: 0
                    ) {
                        Text(desc)
                            .font(NxtBuzFonts.body)
                            .fontWeight(.medium)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        
                        Text(
                            "\(roadName)  •  \(busStopCode)".uppercased()
                        )
                        .font(NxtBuzFonts.caption)
                        .padding(.top, 4)
                        .foregroundColor(
                            Color(nxtBuzTheme.secondaryColor)
                        )
                    }
                }
            }
            
//            Section(
//                header: Text("Dark Mode")
//                    .font(NxtBuzFonts.caption)
//                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
//            ) {
//                Toggle(
//                    isOn: $showGreeting,
//                    label: {
//                        Text("Dark mode")
//                            .font(NxtBuzFonts.body)
//                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
//                            .fontWeight(.medium)
//                    }
//                ).toggleStyle(SwitchToggleStyle(tint: Color(nxtBuzTheme.accentColor)))
//
//                Toggle(
//                    isOn: $showGreeting,
//                    label: {
//                        Text("Use device settings")
//                            .font(NxtBuzFonts.body)
//                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
//                            .fontWeight(.medium)
//                    }
//                ).toggleStyle(SwitchToggleStyle(tint: Color(nxtBuzTheme.accentColor)))
//            }
            
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
                        (Text("Send ") + Text("bugs").strikethrough(true, color: .orange) + Text(" hugs :)"))
                            .font(NxtBuzFonts.body)
                            .fontWeight(.medium)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                        
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
                        (Text("Made by ") + Text("genius").strikethrough(true, color: .orange) + Text(" Amanshu"))
                            .font(NxtBuzFonts.body)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                            .fontWeight(.medium)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.forward")
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                    }
                }
            }
            
            Section(
                header: Text("Tips")
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
            ) {
                WarningBannerView(
                    message: "In a widget, to refresh bus arrival timings, launch the app by clicking on it.",
                    iconSystemName: "lightbulb.fill"
                )
            }
        }
        .listStyle(InsetGroupedListStyle())
        .onAppear {
            viewModel.fetchHomeBusStop()
        }
    }
}

//struct SettingsView_Previews: PreviewProvider {
//    static var previews: some View {
//        SettingsView()
//    }
//}
