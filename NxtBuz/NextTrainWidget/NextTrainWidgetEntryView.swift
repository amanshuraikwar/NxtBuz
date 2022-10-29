//
//  NextTrainWidgetEntryView.swift
//  NextTrainWidgetExtension
//
//  Created by Amanshu Raikwar on 29/10/22.
//

import SwiftUI
import iosUmbrella

struct NextTrainWidgetEntryView : View {
    var entry: Provider.Entry
    
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
        GeometryReader { geometry in
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                VStack(
                    spacing: 0
                ) {
                    HStack(alignment: .top, spacing: 0) {
                        Text(entry.sourceTrainStopName)
                            .font(NxtBuzFonts.body)
                            .fontWeight(.medium)
                        
                        Spacer()
                        
                        Text(entry.departureFromSourceTime.uppercased())
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.bold)
                    }
                    .padding(.horizontal, 8)
                    .padding(.vertical, 6)
                    .background(Color(nxtBuzTheme.accentColor))
                    .cornerRadius(14)
                    .padding(.horizontal, 8)
                    .padding(.top, 8)
                    .foregroundColor(nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
                    
                    HStack(alignment: .top, spacing: 0) {
                        Text(entry.destinationTrainStopName)
                            .font(NxtBuzFonts.body)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                        
                        Spacer()
                        
                        Text(entry.arrivalAtDestinationTime.uppercased())
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.bold)
                            .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                    }
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 8)
                }
                .background(
                    LinearGradient(
                        gradient: Gradient(
                            colors: [Color(.systemGray5), Color(.systemGray6)]
                        ),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .cornerRadius(14)

                HStack(
                    spacing: 0
                ) {
                    Text("\(getTime(date: entry.date))")
                        .font(NxtBuzFonts.caption)
                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
                        .padding(.leading)
                    
                    Spacer()
                    
                    ForEach(entry.facilities, id: \.self) { facility in
                        if facility == TrainFacility.bicycle {
                            Image(systemName: "bicycle")
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .fontWeight(.medium)
                                .frame(width: 18, height: 18)
                                .foregroundColor(Color(nxtBuzTheme.accentColor))
                                .padding(.trailing, 8)
                        }
                        
                        if facility == TrainFacility.wifi {
                            Image(systemName: "wifi")
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .padding(1)
                                .frame(width: 17, height: 17)
                                .foregroundColor(Color(nxtBuzTheme.accentColor))
                                .padding(.trailing, 8)
                        }
                        
                        if facility == TrainFacility.powerSockets {
                            Image(systemName: "powerplug.fill")
                                .renderingMode(.template)
                                .resizable()
                                .scaledToFit()
                                .padding(1)
                                .frame(width: 17, height: 17)
                                .foregroundColor(Color(nxtBuzTheme.accentColor))
                                .padding(.trailing, 8)
                        }
                    }
                    
                    Text(entry.trainId.uppercased())
                        .font(NxtBuzFonts.caption)
                        .fontWeight(.bold)
                        .foregroundColor(Color(nxtBuzTheme.accentColor))
                        .padding(.trailing)
                }
                .padding(.top, 4)
                
                Spacer()
                
                if !entry.rollingStockImages.isEmpty {
                    RollingStockImageView(
                        rollingStockImages: entry.rollingStockImages,
                        maximumViewportWidth: geometry.size.width
                    )
                    .padding(.leading)
                } else {
                    HStack {
                        Spacer()
                        
                        Text(entry.trainType.uppercased())
                            .font(NxtBuzFonts.caption)
                            .fontWeight(.bold)
                            .foregroundColor(.yellow)
                    }
                    .padding(.trailing)
                    .padding(.bottom)
                }
            }
        }
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
        .widgetURL(URL(string: "nextTrainWidget://open")!)
    }
    
    private func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
}
