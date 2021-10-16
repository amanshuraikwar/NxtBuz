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
            ) { searchOutput in
                var characters: [BusArrivalWidgetBusStop] = []

                if let success = searchOutput as? IosSearchOutput.Success {
                    success.searchResult.busStopList.forEach { busStop in
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
                } else if searchOutput is IosSearchOutput.Error {
                    // do nothing
                }
                
                let collection = INObjectCollection<BusArrivalWidgetBusStop>(items: characters)
                completion(collection, nil)
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
                    .invoke(busStopCode: busStopCode) { busServicesList in
                        var characters: [NSString] = []
                        
                        busServicesList.forEach { busService in
                            characters.append(NSString(string: busService))
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
