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

struct StarredBusServiceData : Identifiable {
    let id = UUID()
    let busServiceNumber: String
    let busType: BusType
    let busArrivalDate: Date
}

struct StarredBusStopData : Identifiable {
    let id = UUID()
    let busStopCode: String
    let busStopDescription: String
    let starredBusServiceDataList: [StarredBusServiceData]
}

enum StarredBusArrivalsWidgetState {
    case Error(message: String)
    case NoStarredBuses
    case Success(
        busStopDataList: [StarredBusStopData]
    )
}
