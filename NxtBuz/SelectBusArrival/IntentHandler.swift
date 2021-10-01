//
//  IntentHandler.swift
//  SelectBusArrival
//
//  Created by amanshu raikwar on 01/10/21.
//

import Intents

// As an example, this class is set up to handle Message intents.
// You will want to replace this or add other intents as appropriate.
// The intents you wish to handle must be declared in the extension's Info.plist.

// You can test your example integration by saying things to Siri like:
// "Send a message using <myApp>"
// "<myApp> John saying hello"
// "Search for messages in <myApp>"

class IntentHandler: INExtension, SelectBusArrivalIntentHandling {
    func resolveBusStopCode(for intent: SelectBusArrivalIntent, with completion: @escaping (INStringResolutionResult) -> Void) {
        completion(.success(with: "yello"))
    }
    

    
    func resolveBusServiceNumber(for intent: SelectBusArrivalIntent, with completion: @escaping (INStringResolutionResult) -> Void) {
        
    }
    
    
    func provideBusStopCodeOptionsCollection(for intent: SelectBusArrivalIntent, searchTerm: String?, with completion: @escaping (INObjectCollection<NSString>?, Error?) -> Void) {
        
        var characters: [NSString] = []
        characters.append(NSString(string: "\(searchTerm)1"))
        characters.append(NSString(string: "\(searchTerm)2"))
        characters.append(NSString(string: "\(searchTerm)3"))
        let collection = INObjectCollection<NSString>(items: characters)
        completion(collection, nil)
    }
    
    
    func provideBusServiceNumberOptionsCollection(for intent: SelectBusArrivalIntent, searchTerm: String?, with completion: @escaping (INObjectCollection<NSString>?, Error?) -> Void) {
        
    }
    
    
    override func handler(for intent: INIntent) -> Any {
        // This is the default implementation.  If you want different objects to handle different intents,
        // you can override this and return the handler you want for that particular intent.
        
        return self
    }
}
