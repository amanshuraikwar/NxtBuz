//
//  StarredBusArrivalsWidgetEntry.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 24/10/21.
//

import Foundation
import WidgetKit
import iosUmbrella

struct StarredBusArrivalsWidgetEntry : TimelineEntry {
    let date: Date
    let state: StarredBusArrivalsWidgetState
}

struct ArrivingBusData : Identifiable {
    let id = UUID()
    let busType: BusType
    let busLoad: BusLoad
    let wheelChairAccess: Bool
    let nextArrivalTime: Date
}

struct StarredBusServiceData {
    let busServiceNumber: String
    let nextArrivingBusData: ArrivingBusData
    let followingArrivingBusDataList: [ArrivingBusData]
}

struct StarredBusStopData : Identifiable {
    let id = UUID()
    let busStopCode: String
    let busStopDescription: String
    let starredBusArrivalList: [StarredBusArrival]
}

enum StarredBusArrivalsWidgetState {
    case Error(message: String)
    case NoStarredBuses
    case Success(
        busStopDataList: [StarredBusStopData]
    )
}
