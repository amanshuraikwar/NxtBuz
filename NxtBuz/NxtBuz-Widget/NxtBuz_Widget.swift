//
//  NxtBuz_Widget.swift
//  NxtBuz-Widget
//
//  Created by amanshu raikwar on 22/09/21.
//

import WidgetKit
import SwiftUI
import Intents
import iosUmbrella
import NxtBuz

struct Provider: IntentTimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(
            date: Date(),
            busStopDescription: "Opp Jln Jurong Kechil",
            busServiceNumber: "961M",
            busType: BusType.sd,
            arrivalStr: "04",
            configuration: ConfigurationIntent()
        )
    }

    func getSnapshot(for configuration: ConfigurationIntent, in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let date = Date()
        let entry: SimpleEntry
        
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "ss"
        let stringDate = timeFormatter.string(from: date)
        
        if context.isPreview {
            entry = SimpleEntry(
                date: date,
                busStopDescription: "Opp Jln Jurong Kechil",
                busServiceNumber: "961M",
                busType: BusType.sd,
                arrivalStr: "04",
                configuration: configuration
            )
        } else {
            entry = SimpleEntry(
                date: date,
                busStopDescription: "Opp Jln Jurong Kechil",
                busServiceNumber: "961M",
                busType: BusType.sd,
                arrivalStr: stringDate,
                configuration: configuration
            )
        }
        
        completion(entry)
    }

    func getTimeline(for configuration: ConfigurationIntent, in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        let date = Date()
        let entry: SimpleEntry
        
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "ss"
        let stringDate = timeFormatter.string(from: date)
        
        entry = SimpleEntry(
            date: date,
            busStopDescription: "Opp Jln Jurong Kechil",
            busServiceNumber: "961M",
            busType: BusType.sd,
            arrivalStr: stringDate,
            configuration: configuration
        )
        
        // Create a date that's 15 minutes in the future.
        let nextUpdateDate = Calendar.current.date(byAdding: .minute, value: 1, to: date)!

        // Create the timeline with the entry and a reload policy with the date
        // for the next update.
        let timeline = Timeline(
            entries: [entry],
            policy: .after(nextUpdateDate)
        )
        
        completion(timeline)
    }
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let busStopDescription: String
    let busServiceNumber: String
    let busType: BusType
    let arrivalStr: String
    let configuration: ConfigurationIntent
}

struct NxtBuz_WidgetEntryView : View {
    var entry: Provider.Entry
    let busTypeName: String
    let lastUpdated: String
    
    init(entry: Provider.Entry) {
        self.entry = entry
        
        if (entry.busType == BusType.dd) {
            self.busTypeName = "BusTypeDd"
        } else if (entry.busType == BusType.bd) {
            self.busTypeName = "BusTypeFeeder"
        } else {
            self.busTypeName = "BusTypeNormal"
        }
        
        let date = Date()
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "hh:mm a"
        let stringDate = timeFormatter.string(from: date)
        self.lastUpdated = stringDate
    }
    
    var body: some View {
        VStack {
            Text(entry.busStopDescription)
                .font(NxtBuzFonts.body)
                .fontWeight(.bold)
                .multilineTextAlignment(.leading)
            
            Spacer()
            
            HStack {
                ZStack {
                    Text(entry.busServiceNumber)
                        .font(NxtBuzFonts.headline)
                        .foregroundColor(Color(.systemGray6))
                    
                    Text("961M")
                        .font(NxtBuzFonts.headline)
                        .opacity(0.0)
                }
                .padding(.vertical, 4)
                .padding(.horizontal, 8)
                .background(Color.accentColor)
                .clipShape(Capsule())
                
                Spacer()
                
                Image(systemName: "arrow.right.square.fill")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundColor(Color.primary)
                
                Spacer()
                
                Text(entry.arrivalStr)
                    .font(NxtBuzFonts.headline)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
            }
            
            Spacer()
            
            HStack {
                Image(busTypeName)
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundColor(Color.primary)
                
                Image("BusLoad2")
                    .renderingMode(.template)
                    .resizable()
                    .scaledToFit()
                    .frame(width: 16, height: 16)
                    .foregroundColor(Color.primary)
                
                Spacer()

                Text(lastUpdated)
                    .font(NxtBuzFonts.caption)
                    .foregroundColor(Color.secondary)
            }
        }
        .padding()
    }
}

@main
struct NxtBuz_Widget: Widget {
    let kind: String = "NxtBuz_Widget"

    var body: some WidgetConfiguration {
        IntentConfiguration(kind: kind, intent: ConfigurationIntent.self, provider: Provider()) { entry in
            NxtBuz_WidgetEntryView(entry: entry)
        }
        .supportedFamilies([.systemSmall])
        .configurationDisplayName("My Widget")
        .description("This is an example widget.")
    }
}

//struct NxtBuz_Widget_Previews: PreviewProvider {
//    static var previews: some View {
//        NxtBuz_WidgetEntryView(entry: SimpleEntry(date: Date(), configuration: ConfigurationIntent()))
//            .previewContext(WidgetPreviewContext(family: .systemSmall))
//    }
//}
