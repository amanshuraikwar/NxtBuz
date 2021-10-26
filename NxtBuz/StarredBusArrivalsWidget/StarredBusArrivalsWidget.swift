//
//  StarredBusArrivalsWidget.swift
//  StarredBusArrivalsWidget
//
//  Created by amanshu raikwar on 24/10/21.
//

import WidgetKit
import SwiftUI
import iosUmbrella

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> StarredBusArrivalsWidgetEntry {
        StarredBusArrivalsWidgetEntry(
            date: Date(),
            state: StarredBusArrivalsWidgetState.Success(
                busStopDataList: [
                    StarredBusStopData(
                        busStopCode: "123456",
                        busStopDescription: "Opp Jln Jurong Kechil",
                        starredBusServiceDataList: [
                            StarredBusServiceData(
                                busServiceNumber: "961M",
                                busType: BusType.dd,
                                busArrivalDate: Date()
                            ),
                            StarredBusServiceData(
                                busServiceNumber: "61",
                                busType: BusType.bd,
                                busArrivalDate: Calendar.current.date(
                                    byAdding: .minute,
                                    value: 2,
                                    to: Date()
                                )!
                            )
                        ]
                    ),
                    StarredBusStopData(
                        busStopCode: "123456",
                        busStopDescription: "Opp Blk 19",
                        starredBusServiceDataList: [
                            StarredBusServiceData(
                                busServiceNumber: "77",
                                busType: BusType.sd,
                                busArrivalDate: Calendar.current.date(
                                    byAdding: .minute,
                                    value: 4,
                                    to: Date()
                                )!
                            ),
                            StarredBusServiceData(
                                busServiceNumber: "970",
                                busType: BusType.dd,
                                busArrivalDate: Calendar.current.date(
                                    byAdding: .minute,
                                    value: 6,
                                    to: Date()
                                )!
                            )
                        ]
                    )
                ]
            )
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (StarredBusArrivalsWidgetEntry) -> ()) {
        let entry = StarredBusArrivalsWidgetEntry(
            date: Date(),
            state: StarredBusArrivalsWidgetState.Success(
                busStopDataList: [
                    StarredBusStopData(
                        busStopCode: "123456",
                        busStopDescription: "Opp Jln Jurong Kechil",
                        starredBusServiceDataList: [
                            StarredBusServiceData(
                                busServiceNumber: "961M",
                                busType: BusType.dd,
                                busArrivalDate: Date()
                            ),
                            StarredBusServiceData(
                                busServiceNumber: "61",
                                busType: BusType.bd,
                                busArrivalDate: Calendar.current.date(
                                    byAdding: .minute,
                                    value: 2,
                                    to: Date()
                                )!
                            )
                        ]
                    ),
                    StarredBusStopData(
                        busStopCode: "123456",
                        busStopDescription: "Opp Blk 19",
                        starredBusServiceDataList: [
                            StarredBusServiceData(
                                busServiceNumber: "77",
                                busType: BusType.sd,
                                busArrivalDate: Calendar.current.date(
                                    byAdding: .minute,
                                    value: 4,
                                    to: Date()
                                )!
                            ),
                            StarredBusServiceData(
                                busServiceNumber: "970",
                                busType: BusType.dd,
                                busArrivalDate: Calendar.current.date(
                                    byAdding: .minute,
                                    value: 6,
                                    to: Date()
                                )!
                            )
                        ]
                    )
                ]
            )
        )
        completion(entry)
    }

    func getTimeline(
        in context: Context,
        completion: @escaping (Timeline<StarredBusArrivalsWidgetEntry>) -> ()
    ) {
        Di.get().getUserStateUserCase().invoke { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(let message):
                let entry = StarredBusArrivalsWidgetEntry(
                    date: Date(),
                    state: StarredBusArrivalsWidgetState.Error(
                        message: message
                    )
                )
                
                let timeline = Timeline(
                    entries: [entry],
                    policy: .never
                )
                
                completion(timeline)
            case .Success(let userState):
                if (userState is UserState.New) {
                    let date = Date()
                    let entry = StarredBusArrivalsWidgetEntry(
                        date: date,
                        state: StarredBusArrivalsWidgetState.Error(
                            message: "Please complete seting up the app"
                        )
                    )
                    
                    let timeline = Timeline(
                        entries: [entry],
                        policy: .after(Calendar.current.date(byAdding: .minute, value: 10, to: date)!)
                    )
                    
                    completion(timeline)
                } else {
                    emitStarredBusArrivalsTimeline(completion: completion)
                }
            }
        }
    }
    
    func emitStarredBusArrivalsTimeline(completion: @escaping (Timeline<StarredBusArrivalsWidgetEntry>) -> ()) {
        Di.get()
            .getStarredBusArrivalsUseCase()
            .invoke { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(let message):
                    let entry = StarredBusArrivalsWidgetEntry(
                        date: Date(),
                        state: StarredBusArrivalsWidgetState.Error(
                            message: message
                        )
                    )
                    
                    let timeline = Timeline(
                        entries: [entry],
                        policy: .never
                    )
                    
                    completion(timeline)
                case .Success(let data):
                    let starredBusArrivalList = data.compactMap({ $0 as? StarredBusArrival })
                    if starredBusArrivalList.isEmpty {
                        let entry = StarredBusArrivalsWidgetEntry(
                            date: Date(),
                            state: StarredBusArrivalsWidgetState.NoStarredBuses
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .never
                        )
                        
                        completion(timeline)
                    } else {
                        var busStopDataList: [StarredBusStopData] = []
                        
                        var currentBusStopCode = ""
                        var currentBusStopDescription = ""
                        var currentBusCount = 0
                        var currentStarredBusServiceDataList: [StarredBusServiceData] = []
                        var currentBusStopCount = 1
                        
                        for starredBusArrival in starredBusArrivalList {
                            if currentBusStopCount > StarredBusStopWidgetView.MAX_DISPLAY_BUS_STOPS {
                                break
                            }
                            
                            if currentBusStopCode != starredBusArrival.busStopCode {
                                if !currentStarredBusServiceDataList.isEmpty {
                                    busStopDataList.append(
                                        StarredBusStopData(
                                            busStopCode: currentBusStopCode,
                                            busStopDescription: currentBusStopDescription,
                                            starredBusServiceDataList: currentStarredBusServiceDataList
                                        )
                                    )
                                    currentBusStopCount += 1
                                }
                                currentBusStopCode = starredBusArrival.busStopCode
                                currentBusStopDescription = starredBusArrival.busStop.description_
                                currentStarredBusServiceDataList = []
                                currentBusCount = 0
                            }
                            
                            if currentBusCount != StarredBusStopWidgetView.MAX_DISPLAY_BUSES {
                                if let arriving = starredBusArrival.busArrivals as? BusArrivals.Arriving {
                                    currentStarredBusServiceDataList.append(
                                        StarredBusServiceData(
                                            busServiceNumber: starredBusArrival.busServiceNumber,
                                            busType: arriving.nextArrivingBus.type,
                                            busArrivalDate: arriving.nextArrivingBus.arrivalInstant.toNSDate()
                                        )
                                    )
                                    currentBusCount += 1
                                }
                            }
                        }
                        
                        if currentBusStopCount <= StarredBusStopWidgetView.MAX_DISPLAY_BUS_STOPS {
                            if !currentStarredBusServiceDataList.isEmpty {
                                busStopDataList.append(
                                    StarredBusStopData(
                                        busStopCode: currentBusStopCode,
                                        busStopDescription: currentBusStopDescription,
                                        starredBusServiceDataList: currentStarredBusServiceDataList
                                    )
                                )
                            }
                        }
                        
                        let entry = StarredBusArrivalsWidgetEntry(
                            date: Date(),
                            state: StarredBusArrivalsWidgetState.Success(
                                busStopDataList: busStopDataList
                            )
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(
                                Calendar.current.date(
                                    byAdding: .minute,
                                    value: 5,
                                    to: Date()
                                )!
                            )
                        )
                        
                        completion(timeline)
                    }
            }
        }
    }
}

@main
struct StarredBusArrivalsWidget: Widget {
    let kind: String = "io.github.amanshuraikwar.NxtBuz.starredBusArrivalsWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            StarredBusArrivalsWidgetEntryView(entry: entry)
        }
        .supportedFamilies([.systemLarge])
        .configurationDisplayName("Starred Bus Arrivals")
        .description(
            "See bus arrival timing of up to 6 recently starred buses"
        )
    }
}
