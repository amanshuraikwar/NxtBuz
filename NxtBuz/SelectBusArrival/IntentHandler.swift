//
//  IntentHandler.swift
//  SelectBusArrival
//
//  Created by amanshu raikwar on 01/10/21.
//

import Intents
import iosUmbrella

// As an example, this class is set up to handle Message intents.
// You will want to replace this or add other intents as appropriate.
// The intents you wish to handle must be declared in the extension's Info.plist.

// You can test your example integration by saying things to Siri like:
// "Send a message using <myApp>"
// "<myApp> John saying hello"
// "Search for messages in <myApp>"

class IntentHandler: INExtension, SelectBusArrivalIntentHandling {
    func resolveBusStop(for intent: SelectBusArrivalIntent, with completion: @escaping (BusArrivalWidgetBusStopResolutionResult) -> Void) {
        
    }
    
    func resolveBusServiceNumber(for intent: SelectBusArrivalIntent, with completion: @escaping (INStringResolutionResult) -> Void) {
        completion(.disambiguation(with: ["sbcdrfgh", "sefgf"]))
    }
    
    func provideBusStopOptionsCollection(
        for intent: SelectBusArrivalIntent,
        searchTerm: String?,
        with completion: @escaping (INObjectCollection<BusArrivalWidgetBusStop>?, Error?) -> Void
    ) {
        DispatchQueue.main.sync {
            Di.get().getSearchUseCase().invoke(
                query: searchTerm ?? "",
                limit: 100
            ) { result in
                var characters: [BusArrivalWidgetBusStop] = []
                
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Success(let searchResult):
                    searchResult.busStopList.forEach { busStop in
                        let widgetBusStop = BusArrivalWidgetBusStop(
                            identifier: busStop.code,
                            display: "\(busStop.description_)  •  \(busStop.roadName)"
                        )
                        widgetBusStop.busStopCode = busStop.code
                        widgetBusStop.busStopDescription = busStop.description_
                        
                        characters.append(widgetBusStop)
                    }
                    
                    let collection = INObjectCollection<BusArrivalWidgetBusStop>(items: characters)
                    completion(collection, nil)
                case .Error(let message):
                    print(message)
                    let collection = INObjectCollection<BusArrivalWidgetBusStop>(items: characters)
                    completion(collection, nil)
                }
            }
        }
    }
    
    func provideBusServiceNumberOptionsCollection(
        for intent: SelectBusArrivalIntent,
        with completion: @escaping (INObjectCollection<NSString>?, Error?) -> Void
    ) {
        if let busStopCode = intent.BusStop?.busStopCode {
            DispatchQueue.main.sync {
                Di.get()
                    .getOperatingBusServicesUseCase()
                    .invoke(busStopCode: busStopCode) { result in
                        var characters: [NSString] = []
                        
                        let useCaseResult = Util.toUseCaseResult(result)
                        switch useCaseResult {
                        case .Success(let data):
                            let busServicesList = data.compactMap({ $0 as? Bus })
                            for i in 0...busServicesList.count-1 {
                                characters.append(NSString(string: busServicesList[i].serviceNumber))
                            }
                        case .Error(let message):
                            print(message)
                        }
                        
                        let collection = INObjectCollection<NSString>(items: characters)
                        completion(collection, nil)
                        intent.BusServiceNumber = nil
                    }
            }
        } else {
            completion(nil, nil)
        }
    }
    
    override func handler(for intent: INIntent) -> Any {
        // This is the default implementation.  If you want different objects to handle different intents,
        // you can override this and return the handler you want for that particular intent.
        
        return self
    }
}
