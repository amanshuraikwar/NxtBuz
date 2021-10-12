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
            widgetState: .Arriving(
                busStopCode: "123456",
                busStopDescription: "Opp Jln Jurong Kechil",
                busServiceNumber: "961M",
                nextArrivingBusData: ArrivingBusData(
                    busType: BusType.dd,
                    busLoad: BusLoad.sda,
                    wheelChairAccess: false,
                    nextArrivalTime: Date()
                ),
                followingArrivingBusDataList: [
                    ArrivingBusData(
                        busType: BusType.dd,
                        busLoad: BusLoad.sda,
                        wheelChairAccess: false,
                        nextArrivalTime: Date()
                    ),
                    ArrivingBusData(
                        busType: BusType.dd,
                        busLoad: BusLoad.sda,
                        wheelChairAccess: false,
                        nextArrivalTime: Calendar.current.date(byAdding: .minute, value: 5, to: Date())!
                    ),
                    ArrivingBusData(
                        busType: BusType.dd,
                        busLoad: BusLoad.sda,
                        wheelChairAccess: false,
                        nextArrivalTime: Calendar.current.date(byAdding: .minute, value: 10, to: Date())!
                    )
                ]
            ),
            configuration: SelectBusArrivalIntent()
        )
    }

    func getSnapshot(
        for configuration: SelectBusArrivalIntent,
        in context: Context,
        completion: @escaping (SimpleEntry) -> ()
    ) {
        let entry: SimpleEntry
        
        if context.isPreview {
            entry = SimpleEntry(
                date: Date(),
                widgetState: .Arriving(
                    busStopCode: "123456",
                    busStopDescription: "Opp Jln Jurong Kechil",
                    busServiceNumber: "961M",
                    nextArrivingBusData: ArrivingBusData(
                        busType: BusType.dd,
                        busLoad: BusLoad.sda,
                        wheelChairAccess: false,
                        nextArrivalTime: Date()
                    ),
                    followingArrivingBusDataList: [
                        ArrivingBusData(
                            busType: BusType.dd,
                            busLoad: BusLoad.sda,
                            wheelChairAccess: false,
                            nextArrivalTime: Date()
                        ),
                        ArrivingBusData(
                            busType: BusType.dd,
                            busLoad: BusLoad.sda,
                            wheelChairAccess: false,
                            nextArrivalTime: Calendar.current.date(byAdding: .minute, value: 5, to: Date())!
                        ),
                        ArrivingBusData(
                            busType: BusType.dd,
                            busLoad: BusLoad.sda,
                            wheelChairAccess: false,
                            nextArrivalTime: Calendar.current.date(byAdding: .minute, value: 10, to: Date())!
                        )
                    ]
                ),
                configuration: SelectBusArrivalIntent()
            )
        } else {
            entry = SimpleEntry(
                date: Date(),
                widgetState: .Arriving(
                    busStopCode: "123456",
                    busStopDescription: "Opp Jln Jurong Kechil",
                    busServiceNumber: "961M",
                    nextArrivingBusData: ArrivingBusData(
                        busType: BusType.dd,
                        busLoad: BusLoad.sda,
                        wheelChairAccess: false,
                        nextArrivalTime: Date()
                    ),
                    followingArrivingBusDataList: [
                        ArrivingBusData(
                            busType: BusType.dd,
                            busLoad: BusLoad.sda,
                            wheelChairAccess: false,
                            nextArrivalTime: Date()
                        ),
                        ArrivingBusData(
                            busType: BusType.dd,
                            busLoad: BusLoad.sda,
                            wheelChairAccess: false,
                            nextArrivalTime: Calendar.current.date(byAdding: .minute, value: 5, to: Date())!
                        ),
                        ArrivingBusData(
                            busType: BusType.dd,
                            busLoad: BusLoad.sda,
                            wheelChairAccess: false,
                            nextArrivalTime: Calendar.current.date(byAdding: .minute, value: 10, to: Date())!
                        )
                    ]
                ),
                configuration: SelectBusArrivalIntent()
            )
        }
        
        completion(entry)
    }

    func getTimeline(
        for configuration: SelectBusArrivalIntent,
        in context: Context,
        completion: @escaping (Timeline<Entry>) -> ()
    ) {
        Di.get().getUserStateUserCase().invoke { userState in
            if (userState is UserState.New) {
                let date = Date()
                let entry = SimpleEntry(
                    date: date,
                    widgetState: WidgetState.Error(message: "Please complete seting up the app"),
                    configuration: configuration
                )
                
                let timeline = Timeline(
                    entries: [entry],
                    policy: .after(Calendar.current.date(byAdding: .minute, value: 10, to: date)!)
                )
                
                completion(timeline)
            } else if let busStopCode = configuration.BusStop?.busStopCode,
                let busServiceNumber = configuration.BusServiceNumber {
                
                Di.get()
                    .getBusArrivalsUseCase()
                    .invoke(
                        busStopCode: busStopCode,
                        busServiceNumber: busServiceNumber
                    ) { busStopArrival in
                        let date = Date()
                        let entry: SimpleEntry
                        
                        if let busArrivals = busStopArrival.busArrivals as? BusArrivals.Arriving {
                            var followingArrivingBusDataList: [ArrivingBusData] = []
                            followingArrivingBusDataList.append(
                                ArrivingBusData(
                                    busType: busArrivals.nextArrivingBus.type,
                                    busLoad: busArrivals.nextArrivingBus.load,
                                    wheelChairAccess: busArrivals.nextArrivingBus.wheelchairAccess,
                                    nextArrivalTime: Calendar.current.date(
                                        byAdding: .minute,
                                        value: Int(busArrivals.nextArrivingBus.arrival),
                                        to: date
                                    )!
                                )
                            )
                            
                            busArrivals.followingArrivingBusList.forEach { arrivingBus in
                                followingArrivingBusDataList.append(
                                    ArrivingBusData(
                                        busType: arrivingBus.type,
                                        busLoad: arrivingBus.load,
                                        wheelChairAccess: arrivingBus.wheelchairAccess,
                                        nextArrivalTime: Calendar.current.date(
                                            byAdding: .minute,
                                            value: Int(arrivingBus.arrival),
                                            to: date
                                        )!
                                    )
                                )
                            }
                            
                            entry = SimpleEntry(
                                date: date,
                                widgetState: .Arriving(
                                    busStopCode: busStopArrival.busStopCode,
                                    busStopDescription: busStopArrival.busStopDescription,
                                    busServiceNumber: busStopArrival.busServiceNumber,
                                    nextArrivingBusData: ArrivingBusData(
                                        busType: busArrivals.nextArrivingBus.type,
                                        busLoad: busArrivals.nextArrivingBus.load,
                                        wheelChairAccess: busArrivals.nextArrivingBus.wheelchairAccess,
                                        nextArrivalTime: Calendar.current.date(
                                            byAdding: .minute,
                                            value: Int(busArrivals.nextArrivingBus.arrival),
                                            to: date
                                        )!
                                    ),
                                    followingArrivingBusDataList: followingArrivingBusDataList
                                ),
                                configuration: configuration
                            )
                        } else {
                            let errorMessage: String
                            if busStopArrival.busArrivals is BusArrivals.NotOperating {
                                errorMessage = "Not Operating"
                            } else {
                                errorMessage = "No Data"
                            }
                            
                            entry = SimpleEntry(
                                date: date,
                                widgetState: WidgetState.NotArriving(
                                    busStopCode: busStopArrival.busStopCode,
                                    busStopDescription: busStopArrival.busStopDescription,
                                    busServiceNumber: busStopArrival.busServiceNumber,
                                    errorMessage: errorMessage
                                ),
                                configuration: configuration
                            )
                        }
                        
                        let nextUpdateDate = Calendar.current.date(byAdding: .minute, value: 10, to: date)!

                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(nextUpdateDate)
                        )
                        
                        completion(timeline)
                    }
            } else {
                let entry = SimpleEntry(
                    date: Date(),
                    widgetState: WidgetState.Error(message: "Please configure the widget"),
                    configuration: configuration
                )
                
                let timeline = Timeline(
                    entries: [entry],
                    policy: .never
                )
                
                completion(timeline)
            }
        }
    }
}

struct ArrivingBusData : Identifiable {
    let id = UUID()
    let busType: BusType
    let busLoad: BusLoad
    let wheelChairAccess: Bool
    let nextArrivalTime: Date
}

enum WidgetState {
    case Error(message: String)
    case Arriving(
        busStopCode: String,
        busStopDescription: String,
        busServiceNumber: String,
        nextArrivingBusData: ArrivingBusData,
        followingArrivingBusDataList: [ArrivingBusData]
    )
    case NotArriving(
        busStopCode: String,
        busStopDescription: String,
        busServiceNumber: String,
        errorMessage: String
    )
}

struct SimpleEntry: TimelineEntry {
    let date: Date
    let widgetState: WidgetState
    let configuration: SelectBusArrivalIntent
}

struct NxtBuz_WidgetEntryView : View {
    var entry: Provider.Entry
    
    public func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
    
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var nxtBuzTheme = NxtBuzTheme()
    
    var body: some View {
        ZStack {
            switch(entry.widgetState) {
            case .Arriving(
                let busStopCode,
                let busStopDescription,
                let busServiceNumber,
                let nextArrivingBusData,
                let followingArrivingBusDataList
            ):
                ArrivingBusWidgetView(
                    busStopCode: busStopCode,
                    busStopDescription: busStopDescription,
                    busServiceNumber: busServiceNumber,
                    nextArrivingBusData: nextArrivingBusData,
                    followingArrivingBusDataList: followingArrivingBusDataList,
                    updated: entry.date
                ).widgetURL(URL(string: "busArrivalWidget://open?code=\(busStopCode)&service=\(busServiceNumber)&desc=\(busStopDescription.replacingOccurrences(of: " ", with: ""))")!)
            case .NotArriving(
                let busStopCode,
                let busStopDescription,
                let busServiceNumber, let errorMessage
            ):
                NotArrivingBusWidgetView(
                    busStopCode: busStopCode,
                    busStopDescription: busStopDescription,
                    busServiceNumber: busServiceNumber,
                    errorMessage: errorMessage,
                    updated: entry.date
                )
            case .Error(let message):
                VStack(
                    alignment: .leading,
                    spacing: 0
                ) {
                    Text(message)
                        .font(NxtBuzFonts.title2)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.leading)
                        .fixedSize(horizontal: false, vertical: true)
                    
                    Spacer()
                    
                    HStack {
                        Spacer()
                        
                        Image(systemName: "gearshape.2.fill")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 32, height: 32)
                            .padding(8)
                            .foregroundColor(Color(nxtBuzTheme.accentColor))
                            .background(Color(nxtBuzTheme.accentColor).opacity(0.1))
                            .cornerRadius(8)
                    }
                }
                .padding()
            }
        }
        .environmentObject(nxtBuzTheme)
        .onAppear {
            nxtBuzTheme.initTheme(isSystemInDarkMode: colorScheme == .dark)
        }
        .onChange(of: colorScheme) { colorScheme in
            nxtBuzTheme.onSystemThemeChanged(isDark: colorScheme == .dark)
        }
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
        .supportedFamilies([.systemMedium])
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
