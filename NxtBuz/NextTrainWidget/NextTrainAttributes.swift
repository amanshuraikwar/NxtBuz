//
//  NextTrainAttributes.swift
//  NextTrainWidgetExtension
//
//  Created by Amanshu Raikwar on 8/11/22.
//

// Attributes for the next train live activity

import Foundation
import ActivityKit

struct NextTrainAttributes: ActivityAttributes {
    public typealias TrainStatus = ContentState
    
    public struct ContentState: Codable, Hashable {
        var departureFromSourceTime: String
        var arrivalAtDestinationTime: String
    }
    
    var sourceTrainStopName: String
    var destinationTrainStopName: String
}
