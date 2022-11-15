//
//  NextTrainLiveActivityStarter.swift
//  NextTrainWidgetExtension
//
//  Created by Amanshu Raikwar on 9/11/22.
//

import Foundation
import ActivityKit

@available(iOS 16.1, *)
@available(iOSApplicationExtension 16.1, *)
class NextTrainLiveActivityStarter {
    func start() {
        var future = Calendar.current.date(byAdding: .minute, value: 0, to: Date())!
        future = Calendar.current.date(byAdding: .second, value: 60, to: future)!
        let date = Date.now...future
        let initialContentState = NextTrainAttributes.ContentState(
            departureFromSourceTime: "14:00",
            arrivalAtDestinationTime: "15:03"
        )
        let activityAttributes = NextTrainAttributes(
            sourceTrainStopName: "Amsterdam Centraal",
            destinationTrainStopName: "Utrect Centraal"
        )

        do {
            let activity = try Activity.request(
                attributes: activityAttributes,
                contentState: initialContentState
            )
            print("Requested a pizza delivery Live Activity \(String(describing: activity.id)).")
        } catch (let error) {
            print("Error requesting pizza delivery Live Activity \(error.localizedDescription).")
        }
    }
}
