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

struct Provider: IntentTimelineProvider {
    func placeholder(in context: Context) -> SimpleEntry {
        SimpleEntry(
            date: Date(),
            busStopCode: "00000",
            busStopDescription: "Opp Jln Jurong Kechil",
            busServiceNumber: "961M",
            arriving: true,
            busType: BusType.sd,
            nextArrivalInMins: 4,
            configuration: SelectBusArrivalIntent()
        )
    }

    func getSnapshot(for configuration: SelectBusArrivalIntent, in context: Context, completion: @escaping (SimpleEntry) -> ()) {
        let date = Date()
        let entry: SimpleEntry
        
        if context.isPreview {
            entry = SimpleEntry(
                date: date,
                busStopCode: "00000",
                busStopDescription: "Opp Jln Jurong Kechil",
                busServiceNumber: "961M",
                arriving: true,
                busType: BusType.sd,
                nextArrivalInMins: 4,
                configuration: configuration
            )
        } else {
            entry = SimpleEntry(
                date: date,
                busStopCode: "00000",
                busStopDescription: "Opp Jln Jurong Kechil",
                busServiceNumber: "961M",
                arriving: true,
                busType: BusType.sd,
                nextArrivalInMins: 4,
                configuration: configuration
            )
        }
        
        completion(entry)
    }

    func getTimeline(for configuration: SelectBusArrivalIntent, in context: Context, completion: @escaping (Timeline<Entry>) -> ()) {
        
        Di.get().getUserStateUserCase().invoke { userState in
            Di.get().getBusArrivalsUseCase().invoke(
                busStopCode: "42071",
                busServiceNumber: "61"
            ) { busStopArrival in
                let date = Date()
                let entry: SimpleEntry
                
                var x = -1
                
                if let _ = userState as? UserState.New {
                    x = 0
                }
                
                if let _ = userState as? UserState.SetupComplete {
                    x = 1
                }
                
                let y = UserDefaults(suiteName: "group.io.github.amanshuraikwar.NxtBuz")!.bool(forKey: "helohelo")
                
                let z = UserDefaults(suiteName: "group.io.github.amanshuraikwar.NxtBuz")!.bool(forKey: "delodelo")
                
                if let busArrivals = busStopArrival.busArrivals as? BusArrivals.Arriving {
                    entry = SimpleEntry(
                        date: date,
                        busStopCode: busStopArrival.busStopCode,
                        busStopDescription: "\(x) \(y) \(z)",
                        busServiceNumber: busStopArrival.busServiceNumber,
                        arriving: true,
                        busType: busArrivals.nextArrivingBus.type,
                        nextArrivalInMins: Int(busArrivals.nextArrivingBus.arrival),
                        configuration: configuration
                    )
                } else {
                    entry = SimpleEntry(
                        date: date,
                        busStopCode: "42071",//busStopArrival.busStopCode,
                        busStopDescription: "\(userState)",
                        busServiceNumber: "61",//busStopArrival.busServiceNumber,
                        arriving: true,//false,
                        busType: BusType.sd,
                        nextArrivalInMins: 5,
                        configuration: configuration
                    )
                }
                
                // Create a date that's 15 minutes in the future.
                let nextUpdateDate = Calendar.current.date(byAdding: .minute, value: 5, to: date)!

                // Create the timeline with the entry and a reload policy with the date
                // for the next update.
                let timeline = Timeline(
                    entries: [entry],
                    policy: .after(nextUpdateDate)
                )
                
                completion(timeline)
            }
        }
    }
}

enum WidgetState {
    case SetupNotComplete
    case Arriving(
        busStopCode: String,
        busStopDescription: String,
        busServiceNumber: String,
        busType: BusType,
        nextArrivalTime: Date
    )
}
struct SimpleEntry: TimelineEntry {
    let date: Date
    let busStopCode: String
    let busStopDescription: String
    let busServiceNumber: String
    let arriving: Bool
    let busType: BusType
    let nextArrivalInMins: Int
    let configuration: SelectBusArrivalIntent
}

struct NxtBuz_WidgetEntryView : View {
    var entry: Provider.Entry
    let busTypeName: String
    let lastUpdated: String
    let arrivalStr: String
    
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
        
        if self.entry.nextArrivalInMins > 60 {
            self.arrivalStr = "60+"
        } else if self.entry.nextArrivalInMins > 0 {
            self.arrivalStr = String(format: "%02d", self.entry.nextArrivalInMins)
        } else {
            self.arrivalStr = "ARR"
        }
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
                
                if (entry.arriving) {
                    Text(arrivalStr)
                        .font(NxtBuzFonts.headline)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                } else {
                    Text("N/A")
                        .font(NxtBuzFonts.headline)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                }
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
    let kind: String = "io.github.amanshuraikwar.NxtBuz.busArrivalWidget"

    var body: some WidgetConfiguration {
        IntentConfiguration(
            kind: kind,
            intent: SelectBusArrivalIntent.self,
            provider: Provider()
        ) { entry in
            NxtBuz_WidgetEntryView(entry: entry)
        }
        .supportedFamilies([.systemSmall])
        .configurationDisplayName("Bus Arrival Timing")
        .description(
            "See approximate bus arrival timing of a bus service at a bus stop"
        )
    }
}

//struct NxtBuz_Widget_Previews: PreviewProvider {
//    static var previews: some View {
//        NxtBuz_WidgetEntryView(entry: SimpleEntry(date: Date(), configuration: ConfigurationIntent()))
//            .previewContext(WidgetPreviewContext(family: .systemSmall))
//    }
//}
