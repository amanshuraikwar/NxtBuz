//
//  StarredBusArrivalsWidgetEntryView.swift
//  StarredBusArrivalsWidgetExtension
//
//  Created by amanshu raikwar on 24/10/21.
//

import SwiftUI
import iosUmbrella

struct StarredBusArrivalsWidgetEntryView: View {
    var entry: Provider.Entry
    
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
        GeometryReader { geometry in
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                switch entry.state {
                case .Error(let message):
                    VStack(
                        alignment: .leading,
                        spacing: 0
                    ) {
                        Text(message)
                            .font(NxtBuzFonts.title2)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                            .multilineTextAlignment(.leading)
                            .padding(.trailing)
                        
                        Spacer()

                        StarredBusArrivalWidgetFooterView(date: entry.date)
                    }
                    .padding()
                case .NoStarredBuses:
                    VStack(
                        alignment: .leading,
                        spacing: 0
                    ) {
                        Text("Your starred buses will show up here.")
                            .font(NxtBuzFonts.title2)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                            .multilineTextAlignment(.leading)
                            .padding(.trailing)
                        
                        Spacer()

                        StarredBusArrivalWidgetFooterView(date: entry.date)
                    }
                    .padding()
                case .Success(let busStopDataList):
                    if busStopDataList.isEmpty {
                        VStack(
                            alignment: .leading,
                            spacing: 0
                        ) {
                            Text("No starred buses are arriving right now.")
                                .font(NxtBuzFonts.title2)
                                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                                .multilineTextAlignment(.leading)
                                .padding(.trailing)
                            
                            Spacer()

                            StarredBusArrivalWidgetFooterView(date: entry.date)
                        }
                        .padding()
                    } else {
                        ZStack {
                            HStack(
                                alignment: .top,
                                spacing: 0
                            ) {
                                ZStack {
                                    // nothing
                                }
                                .padding(.leading)
                                .padding(.trailing, 8)
                                .frame(width: geometry.size.width * 2 / 5, height: geometry.size.height)
                                
                                
                                ZStack {
                                    // nothing
                                }
                                .padding(.leading, 8)
                                .padding(.trailing)
                                .frame(width: geometry.size.width * 3 / 5, height: geometry.size.height)
                                .background(Color(nxtBuzTheme.accentColor))
                            }
                            
                            VStack(
                                alignment: .leading,
                                spacing: 0
                            ) {
                                ForEach(busStopDataList) { busStopData in
                                    StarredBusStopWidgetView(
                                        busStopData: busStopData,
                                        firstColumnWidth: geometry.size.width * 2 / 5,
                                        secondColumnWidth: geometry.size.width * 3 / 5
                                    )
                                    
                                    Divider()
                                        .padding(.top, 8)
                                        .padding(.bottom, 12)
                                }
                                
                                Spacer()

                                StarredBusArrivalWidgetFooterView(date: entry.date)
                                    .padding(.horizontal)
                            }
                            .padding(.vertical)
                        }
                    }
                }
            }
        }
        .widgetURL(URL(string: "starredBusArrivalsWidget://open")!)
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
    }
}

//struct StarredBusArrivalsWidgetEntryView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsWidgetEntryView()
//    }
//}
