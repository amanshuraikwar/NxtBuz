//
//  GoingHomeBusWidgetEntryView.swift
//  GoingHomeBusWidgetExtension
//
//  Created by amanshu raikwar on 26/10/21.
//

import SwiftUI

struct GoingHomeBusWidgetEntryView : View {
    var entry: GoingHomeBusEntry

    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
        ZStack {
            switch entry.state {
            case .Error(let message):
                GoingHomeBusWidgetInfoView(message: message)
                    .padding()
            case .TooCloseToHome(let homeBusStopDescription):
                GoingHomeBusWidgetInfoView(message: "You are already near \(homeBusStopDescription)")
                    .padding()
            case .NoBusesGoingHome(let homeBusStopDescription):
                GoingHomeBusWidgetInfoView(message: "No direct buses to \(homeBusStopDescription)")
                    .padding()
            case .HomeBusStopNotSet:
                GoingHomeBusWidgetInfoView(message: "Home bus stop not set yet")
                    .padding()
            case .NoBusStopsNearby:
                GoingHomeBusWidgetInfoView(message: "No bus stops nearby")
                    .padding()
            case .LocationUnknown:
                GoingHomeBusWidgetLocationUnknownView()
                    .padding()
            case .Success(
                let busServiceNumber,
                let sourceBusStopDescription,
                _,
                _,
                let distance
            ):
                VStack(
                    alignment: .leading,
                    spacing: 0
                ) {
                    HStack(
                        alignment: .top,
                        spacing: 0
                    ) {
                        BusServiceNumberView(busServiceNumber: busServiceNumber, error: false)
                        
                        Spacer()
                        
                        Text("\(Int(distance)) KM")
                            .font(NxtBuzFonts.caption)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                            .multilineTextAlignment(.trailing)
                    }
                    .padding(.horizontal)
                    .padding(.top)
                    
                    Spacer()
                    
                    VStack(
                        spacing: 0
                    ) {
                        Text(sourceBusStopDescription)
                            .font(NxtBuzFonts.caption)
                            .fontWeight(.medium)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .foregroundColor(Color(nxtBuzTheme.primaryColor))
                            .multilineTextAlignment(.leading)
                            .lineLimit(2)
                            .fixedSize(horizontal: false, vertical: true)
                        
                        HStack(
                            spacing: 0
                        ) {
                            VStack(
                                spacing: 0
                            ) {
                                Divider()
                            }
                            
                            Image(systemName: "arrow.down.circle.fill")
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .frame(width: 12, height: 12)
                        }
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                        
                        Text("GOING HOME")
                            .font(NxtBuzFonts.caption)
                            .fontWeight(.bold)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .foregroundColor(Color(nxtBuzTheme.accentColor))
                            .multilineTextAlignment(.leading)
                            .lineLimit(1)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .padding(8)
                    .background(Color(.systemGray5))
                    .cornerRadius(14)
                    .padding(.horizontal, 8)
                    .padding(.bottom, 8)
                }
            }
        }
        .background(
            LinearGradient(
                gradient: nxtBuzTheme.isDark ? Gradient(colors: [Color(.systemGray5), Color(.systemGray6)]) : Gradient(colors: [Color(.systemGray6), Color(.white)]),
                startPoint: .top,
                endPoint: .bottom
            )
        )
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
        .widgetURL(URL(string: "goingHomeBusWidget://open")!)
    }
}

//struct GoingHomeBusWidgetEntryView_Previews: PreviewProvider {
//    static var previews: some View {
//        GoingHomeBusWidgetEntryView()
//    }
//}
