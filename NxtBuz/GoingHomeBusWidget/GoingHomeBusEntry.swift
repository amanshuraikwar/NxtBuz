//
//  GoingHomeBusEntry.swift
//  NxtBuz
//
//  Created by amanshu raikwar on 26/10/21.
//

import Foundation
import WidgetKit

struct GoingHomeBusEntry: TimelineEntry {
    let date: Date
    let state: GoingHomeBusWidgetState
}

enum GoingHomeBusWidgetState {
    case TooCloseToHome(
        homeBusStopDescription: String
    )
    case NoBusesGoingHome(
        homeBusStopDescription: String
    )
    case LocationUnknown
    case NoBusStopsNearby
    case HomeBusStopNotSet
    case Success(
        busServiceNumber: String,
        sourceBusStopDescription: String,
        destinationBusStopDescription: String,
        stops: Int,
        distance: Double
    )
    case Error(
        message: String
    )
}
