//
//  IntentHandler.swift
//  NextTrainWidgetIntentExtension
//
//  Created by Amanshu Raikwar on 29/10/22.
//

import Intents
import iosUmbrella

class IntentHandler: INExtension, NextTrainWidgetConfigurationIntentHandling {
    
    func provideFromTrainStopOptionsCollection(
        for intent: NextTrainWidgetConfigurationIntent,
        searchTerm: String?,
        with completion:
        @escaping (INObjectCollection<TrainStopInfo>?, Error?) -> Void
    ) {
        DispatchQueue.main.sync {
            Di.get().getSearchTrainStopsUseCase().invokeCallback(
                trainStopName: searchTerm ?? ""
            ) { result in
                var trainStopsInfoList: [TrainStopInfo] = []
                
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Success(let trainStops):
                    let trainStops = trainStops as! [TrainStop]
                    trainStops.forEach { trainStop in
                        let trainStopInfo = TrainStopInfo(
                            identifier: trainStop.code,
                            display: trainStop.name
                        )
                        trainStopInfo.trainStopCode = trainStop.code
                        trainStopInfo.trainStopName = trainStop.name
                        trainStopsInfoList.append(
                            trainStopInfo
                        )
                    }
                case .Error(let message):
                    print(message)
                }
                
                let collection = INObjectCollection<TrainStopInfo>(
                    items: trainStopsInfoList
                )
                completion(collection, nil)
            }
        }
    }
    
//    func provideFromTrainStopOptionsCollection(
//        for intent: NextTrainWidgetConfigurationIntent,
//        searchTerm: String?
//    ) async throws -> INObjectCollection<TrainStopInfo> {
//        <#code#>
//    }
    
    func provideToTrainStopOptionsCollection(
        for intent: NextTrainWidgetConfigurationIntent,
        searchTerm: String?,
        with completion:
        @escaping (INObjectCollection<TrainStopInfo>?, Error?) -> Void
    ) {
        
    }
    
//    func provideToTrainStopOptionsCollection(
//        for intent: NextTrainWidgetConfigurationIntent,
//        searchTerm: String?
//    ) async throws -> INObjectCollection<TrainStopInfo> {
//        <#code#>
//    }
    
    
    override func handler(for intent: INIntent) -> Any {
        // This is the default implementation.  If you want different objects to handle different intents,
        // you can override this and return the handler you want for that particular intent.
        
        return self
    }
}
