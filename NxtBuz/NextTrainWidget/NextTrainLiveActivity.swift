//
//  NextTrainLiveActivity.swift
//  NextTrainWidgetExtension
//
//  Created by Amanshu Raikwar on 8/11/22.
//

import SwiftUI
import WidgetKit

@available(iOSApplicationExtension 16.1, *)
struct NextTrainLiveActivity: Widget {
    var body: some WidgetConfiguration {
        ActivityConfiguration(for: NextTrainAttributes.self) { context in
            // Create the presentation that appears on the Lock Screen and as a
            // banner on the Home Screen of devices that don't support the
            // Dynamic Island.
            // ...
            LockScreenLiveActivityView(context: context)
        } dynamicIsland: { context in
            // Create the presentations that appear in the Dynamic Island.
            DynamicIsland {
                // Create the expanded presentation.
                // ...
                // Create the expanded presentation.
                DynamicIslandExpandedRegion(.leading) {
                    Label(context.attributes.sourceTrainStopName, systemImage: "train")
                        .foregroundColor(.indigo)
                        .font(.title2)
                }
                
                DynamicIslandExpandedRegion(.trailing) {
//                    Label {
//                        Text(timerInterval: context.state.deliveryTimer, countsDown: true)
//                            .multilineTextAlignment(.trailing)
//                            .frame(width: 50)
//                            .monospacedDigit()
//                    } icon: {
//                        Image(systemName: "timer")
//                            .foregroundColor(.indigo)
//                    }
//                    .font(.title2)
                    Text(context.state.departureFromSourceTime.uppercased())
                        .font(NxtBuzFonts.captionMonospaced)
                        .fontWeight(.bold)
                }
                
                DynamicIslandExpandedRegion(.center) {
                    Text(context.attributes.sourceTrainStopName)
                        .lineLimit(1)
                        .font(NxtBuzFonts.caption)
                }
                
                DynamicIslandExpandedRegion(.bottom) {
                    Button {
                        // Deep link into your app.
                    } label: {
                        Label("Call driver", systemImage: "phone")
                    }
                    .foregroundColor(.indigo)
                }
            } compactLeading: {
                HStack(
                    alignment: .center,
                    spacing: 2
                ) {
                    Image(systemName: "train.side.front.car")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(.yellow)
                        .rotation3DEffect(.degrees(180), axis: (x: 0, y: 1, z: 0))
                    
                    Image(systemName: "train.side.middle.car")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(.yellow)
                    
//                    Image(systemName: "train.side.rear.car")
//                        .renderingMode(.template)
//                        .resizable()
//                        .scaledToFit()
//                        .frame(width: 16, height: 16)
//                        .foregroundColor(.yellow)
//                        .rotation3DEffect(.degrees(180), axis: (x: 0, y: 1, z: 0))
                }
            } compactTrailing: {
                Text(context.state.departureFromSourceTime.uppercased())
                    .font(NxtBuzFonts.captionMonospaced)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
                    .frame(width: 40)
                
            } minimal: {
//                VStack(alignment: .center) {
//                    Image(systemName: "timer")
//                    Text(timerInterval: context.state.deliveryTimer, countsDown: true)
//                        .multilineTextAlignment(.center)
//                        .monospacedDigit()
//                        .font(.caption2)
//                }
                Text(context.state.departureFromSourceTime.uppercased())
                    .font(NxtBuzFonts.captionMonospaced)
                    .fontWeight(.bold)
//                    .multilineTextAlignment(.center)
//                    .frame(width: 40)
            }
            .keylineTint(.cyan)
        }
    }
}

@available(iOSApplicationExtension 16.1, *)
struct LockScreenLiveActivityView: View {
    let context: ActivityViewContext<NextTrainAttributes>
    
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
//        GeometryReader { geometry in
            VStack(
                alignment: .leading,
                spacing: 0
            ) {
                VStack(
                    spacing: 0
                ) {
                    HStack(alignment: .center, spacing: 0) {
                        Text(context.attributes.sourceTrainStopName)
                            .font(NxtBuzFonts.body)
                            .fontWeight(.medium)
                        
//                        if let track = entry.sourceTrainStopTrack {
//                            Text(track.uppercased())
//                                .font(NxtBuzFonts.captionSmall)
//                                .fontWeight(.bold)
//                                .padding(.horizontal, 2)
//                                .background(nxtBuzTheme.isDark ? Color(.systemGray6) : .white)
//                                .foregroundColor(Color(nxtBuzTheme.accentColor))
//                                .cornerRadius(2)
//                                .padding(.leading, 4)
//                        }
                         
                        Spacer()
                        
                        Text(context.state.departureFromSourceTime.uppercased())
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
                        Text(context.attributes.destinationTrainStopName)
                            .font(NxtBuzFonts.body)
                        
                        Spacer()
                        
                        Text(context.state.arrivalAtDestinationTime.uppercased())
                            .font(NxtBuzFonts.bodyMonospaced)
                            .fontWeight(.bold)
                    }
                    .foregroundColor(
                        Color(
                            nxtBuzTheme.isDark ? nxtBuzTheme.secondaryColor : .systemGray5
                        )
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 8)
                    .padding(.bottom, 8)
                }
                .background(
                    LinearGradient(
                        gradient: Gradient(
                            colors: nxtBuzTheme.isDark
                            ? [Color(.systemGray5), Color(.systemGray6)]
                            : [Color(nxtBuzTheme.primaryColor), Color(nxtBuzTheme.secondaryColor)]
                        ),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .cornerRadius(14)

//                HStack(
//                    alignment: .center,
//                    spacing: 0
//                ) {
//                    Text("\(getTime(date: entry.date))")
//                        .font(NxtBuzFonts.caption)
//                        .foregroundColor(Color(nxtBuzTheme.secondaryColor))
//                        .padding(.leading)
//
//                    Spacer()
//
//                    ForEach(entry.facilities, id: \.self) { facility in
//                        if facility == TrainFacility.bicycle {
//                            Image(systemName: "bicycle")
//                                .renderingMode(.template)
//                                .resizable()
//                                .scaledToFit()
//                                .fontWeight(.medium)
//                                .frame(width: 18, height: 18)
//                                .foregroundColor(Color(nxtBuzTheme.accentColor))
//                                .padding(.trailing, 8)
//                        }
//
//                        if facility == TrainFacility.wifi {
//                            Image(systemName: "wifi")
//                                .renderingMode(.template)
//                                .resizable()
//                                .scaledToFit()
//                                .padding(1)
//                                .frame(width: 17, height: 17)
//                                .foregroundColor(Color(nxtBuzTheme.accentColor))
//                                .padding(.trailing, 8)
//                        }
//
//                        if facility == TrainFacility.powerSockets {
//                            Image(systemName: "powerplug")
//                                .renderingMode(.template)
//                                .resizable()
//                                .scaledToFit()
//                                .fontWeight(.medium)
//                                .padding(1)
//                                .frame(width: 17, height: 17)
//                                .foregroundColor(Color(nxtBuzTheme.accentColor))
//                                .padding(.trailing, 8)
//                        }
//                    }
//
//                    Text(entry.trainId.uppercased())
//                        .font(NxtBuzFonts.caption)
//                        .fontWeight(.bold)
//                        .foregroundColor(Color(nxtBuzTheme.accentColor))
//                        .padding(.trailing)
//                }
//                .padding(.top, 4)
                
                Spacer()
                
//                if !entry.rollingStockImages.isEmpty {
//                    RollingStockImageView(
//                        rollingStockImages: entry.rollingStockImages,
//                        maximumViewportWidth: geometry.size.width
//                    )
//                    .padding(.leading)
//                } else {
                    HStack(
                        alignment: .center,
                        spacing: 4
                    ) {
                        Image(systemName: "train.side.front.car")
                            .renderingMode(.template)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(.yellow)
                            .rotation3DEffect(.degrees(180), axis: (x: 0, y: 1, z: 0))
                        
                        Image(systemName: "train.side.middle.car")
                            .renderingMode(.template)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(.yellow)
                        
                        Image(systemName: "train.side.rear.car")
                            .renderingMode(.template)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 16, height: 16)
                            .foregroundColor(.yellow)
                            .rotation3DEffect(.degrees(180), axis: (x: 0, y: 1, z: 0))
                        
                        Spacer()
                        
//                        Text(entry.trainType.uppercased())
//                            .font(NxtBuzFonts.caption)
//                            .fontWeight(.bold)
//                            .foregroundColor(.yellow)
                    }
                    .padding(.horizontal)
                    .padding(.bottom)
//                }
            }
//        }
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
    }
}
