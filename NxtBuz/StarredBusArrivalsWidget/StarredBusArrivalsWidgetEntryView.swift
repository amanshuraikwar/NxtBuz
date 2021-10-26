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

                        HStack(
                            alignment: .bottom
                        ) {
                            Text("\(getTime(date: entry.date))")
                                .font(NxtBuzFonts.caption)
                                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                                .frame(maxWidth: .infinity, alignment: .leading)
                                
                            Spacer()
                            
                            Image(systemName: "star.fill")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                                .foregroundColor(.yellow)
                        }
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

                        HStack(
                            alignment: .bottom
                        ) {
                            Text("\(getTime(date: entry.date))")
                                .font(NxtBuzFonts.caption)
                                .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                                .frame(maxWidth: .infinity, alignment: .leading)
                                
                            Spacer()
                            
                            Image(systemName: "star.fill")
                                .resizable()
                                .scaledToFit()
                                .frame(width: 16, height: 16)
                                .foregroundColor(.yellow)
                        }
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

                            HStack(
                                alignment: .bottom
                            ) {
                                Text("\(getTime(date: entry.date))")
                                    .font(NxtBuzFonts.caption)
                                    .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                                    .frame(maxWidth: .infinity, alignment: .leading)
                                    
                                Spacer()
                                
                                Image(systemName: "star.fill")
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 16, height: 16)
                                    .foregroundColor(.yellow)
                            }
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

                                HStack(
                                    alignment: .bottom
                                ) {
                                    Text("\(getTime(date: entry.date))")
                                        .font(NxtBuzFonts.caption)
                                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                                        .frame(maxWidth: .infinity, alignment: .leading)
                                        
                                    Spacer()
                                    
                                    Image(systemName: "star.fill")
                                        .resizable()
                                        .scaledToFit()
                                        .frame(width: 16, height: 16)
                                        .foregroundColor(.yellow)
                                }
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
    
    public func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}

//struct StarredBusArrivalsWidgetEntryView_Previews: PreviewProvider {
//    static var previews: some View {
//        StarredBusArrivalsWidgetEntryView()
//    }
//}
