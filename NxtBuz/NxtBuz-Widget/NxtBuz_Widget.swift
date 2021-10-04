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
                busType: BusType.dd,
                busLoad: BusLoad.sda,
                wheelChairAccess: false,
                nextArrivalTime: Date()
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
                    busType: BusType.dd,
                    busLoad: BusLoad.sda,
                    wheelChairAccess: false,
                    nextArrivalTime: Date()
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
                    busType: BusType.dd,
                    busLoad: BusLoad.sda,
                    wheelChairAccess: false,
                    nextArrivalTime: Date()
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
        if !UserDefaults(suiteName: "group.io.github.amanshuraikwar.NxtBuz")!.bool(forKey: "setupComplete") {
            let date = Date()
            let entry = SimpleEntry(
                date: date,
                widgetState: WidgetState.Error(message: "Please complete seting up the app."),
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
                        entry = SimpleEntry(
                            date: date,
                            widgetState: .Arriving(
                                busStopCode: busStopArrival.busStopCode,
                                busStopDescription: busStopArrival.busStopDescription,
                                busServiceNumber: busStopArrival.busServiceNumber,
                                busType: busArrivals.nextArrivingBus.type,
                                busLoad: busArrivals.nextArrivingBus.load,
                                wheelChairAccess: busArrivals.nextArrivingBus.wheelchairAccess,
                                nextArrivalTime: Calendar.current.date(
                                    byAdding: .minute,
                                    value: Int(busArrivals.nextArrivingBus.arrival),
                                    to: date
                                )!
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
                widgetState: WidgetState.Error(message: "Please configure the widget."),
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

enum WidgetState {
    case Error(message: String)
    case Arriving(
        busStopCode: String,
        busStopDescription: String,
        busServiceNumber: String,
        busType: BusType,
        busLoad: BusLoad,
        wheelChairAccess: Bool,
        nextArrivalTime: Date
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
    
    private func getBusTypeImageName(_ busType: BusType) -> String {
        if (busType == BusType.dd) {
            return "BusTypeDd"
        } else if (busType == BusType.bd) {
            return "BusTypeFeeder"
        } else {
            return "BusTypeNormal"
        }
    }
    
    private func getBusLoadImageName(_ busLoad: BusLoad) -> String {
        if busLoad == BusLoad.sea {
            return "BusLoad1"
        } else if busLoad == BusLoad.sda {
            return "BusLoad2"
        } else if busLoad == BusLoad.lsd {
            return "BusLoad3"
        } else {
            return "BusLoad0"
        }
    }
    
    private func getWheelCharAccessImageName(_ wheelchairAccess: Bool) -> String {
        if wheelchairAccess {
            return "Accessible"
        } else {
            return "NotAccessible"
        }
    }
    
    public func getTime(date time: Date) -> String {
        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "h:mm a"
        let stringDate = timeFormatter.string(from: time)
        return stringDate
    }
    
    var body: some View {
        switch(entry.widgetState) {
        case .Arriving(
            _,
            let busStopDescription,
            let busServiceNumber,
            let busType,
            let busLoad,
            _,
            let nextArrivalTime
        ):
            VStack(
                spacing: 0
            ) {
                HStack {
                    ZStack {
                        Text(busServiceNumber)
                            .font(NxtBuzFonts.title3)
                            .fontWeight(.medium)
                            .foregroundColor(Color(.white))
                        
                        Text("961M")
                            .font(NxtBuzFonts.title3)
                            .fontWeight(.medium)
                            .opacity(0.0)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .background(Color.accentColor)
                    .clipShape(Capsule())
                    
                    Spacer()

                    Image(getBusTypeImageName(busType))
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.primary)

                    Image(getBusLoadImageName(busLoad))
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.primary)
                }

                Text(busStopDescription)
                    .font(NxtBuzFonts.caption)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 8)
                    .foregroundColor(Color.primary)
                    .multilineTextAlignment(.leading)
                    .fixedSize(horizontal: false, vertical: true)

                Spacer()
                
                Text(getTime(date: nextArrivalTime))
                    .font(NxtBuzFonts.title)
                    .fontWeight(.bold)
                    .foregroundColor(Color.primary)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding()
        case .NotArriving(
            let busStopCode,
            let busStopDescription,
            let busServiceNumber, let errorMessage
        ):
            VStack(
                spacing: 0
            ) {
                HStack {
                    ZStack {
                        Text(busServiceNumber)
                            .font(NxtBuzFonts.title3)
                            .fontWeight(.medium)
                            .foregroundColor(Color(.systemGray5))
                        
                        Text("961M")
                            .font(NxtBuzFonts.title3)
                            .fontWeight(.medium)
                            .opacity(0.0)
                    }
                    .padding(.vertical, 2)
                    .padding(.horizontal, 4)
                    .background(Color(.systemGray))
                    .clipShape(Capsule())
                    
                    Spacer()

                    Image(getBusTypeImageName(BusType.sd))
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.secondary)

                    Image("BusLoad0")
                        .renderingMode(.template)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 16, height: 16)
                        .foregroundColor(Color.secondary)
                }

                Text(busStopDescription)
                    .font(NxtBuzFonts.caption)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.top, 8)
                    .multilineTextAlignment(.leading)
                    .foregroundColor(Color.secondary)
                    .fixedSize(horizontal: false, vertical: true)

                Spacer()
                
                Text(errorMessage)
                    .font(NxtBuzFonts.title2)
                    .fontWeight(.bold)
                    .foregroundColor(Color.secondary)
                    .multilineTextAlignment(.leading)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding()
        case .Error(let message):
            VStack(
                alignment: .leading
            ) {
                Image(systemName: "gearshape.2.fill")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 32, height: 32)
                    .padding(8)
                    .foregroundColor(Color.accentColor)
                    .background(Color.accentColor.opacity(0.1))
                    .cornerRadius(8)
                
                Spacer()
                
                Text(message)
                    .font(NxtBuzFonts.footnote)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.leading)
            }
            .padding()
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
