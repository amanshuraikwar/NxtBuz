//
//  GoingHomeBusWidget.swift
//  GoingHomeBusWidget
//
//  Created by amanshu raikwar on 26/10/21.
//

import WidgetKit
import SwiftUI
import iosUmbrella
import CoreLocation

class Provider: TimelineProvider {
    func placeholder(in context: Context) -> GoingHomeBusEntry {
        GoingHomeBusEntry(
            date: Date(),
            state: GoingHomeBusWidgetState.Success(
                busServiceNumber: "961M",
                sourceBusStopDescription: "Beauty World Stn Exit C",
                destinationBusStopDescription: "Opp Blk 19",
                stops: 14,
                distance: 9.0
            )
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (GoingHomeBusEntry) -> ()) {
        completion(
            GoingHomeBusEntry(
                date: Date(),
                state: GoingHomeBusWidgetState.Success(
                    busServiceNumber: "961M",
                    sourceBusStopDescription: "Beauty World Stn Exit C",
                    destinationBusStopDescription: "Opp Blk 19",
                    stops: 14,
                    distance: 9.0
                )
            )
        )
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<GoingHomeBusEntry>) -> ()) {
        Di.get().getUserStateUserCase().invoke { result in
            let useCaseResult = Util.toUseCaseResult(result)
            switch useCaseResult {
            case .Error(let message):
                let entry = GoingHomeBusEntry(
                    date: Date(),
                    state: GoingHomeBusWidgetState.Error(
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
                    let entry = GoingHomeBusEntry(
                        date: date,
                        state: GoingHomeBusWidgetState.Error(
                            message: "Please complete seting up the app"
                        )
                    )
                    
                    let timeline = Timeline(
                        entries: [entry],
                        policy: .after(Calendar.current.date(byAdding: .minute, value: 10, to: date)!)
                    )
                    
                    completion(timeline)
                } else {
                    self.emitGoingHomeBusTimeline(completion: completion)
                }
            }
        }
    }
    
    func emitGoingHomeBusTimeline(
        completion: @escaping (Timeline<GoingHomeBusEntry>) -> ()
    ) {
        let locationManager = CLLocationManager()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        let date = Date()
        var state: GoingHomeBusWidgetState = GoingHomeBusWidgetState.LocationUnknown
        
        var lat = -1.0
        var lng = -1.0
        
        switch locationManager.authorizationStatus {
        case .notDetermined, .denied:
            state = GoingHomeBusWidgetState.Error(
                message: "Please enable location access."
            )
        case .restricted:
            state = GoingHomeBusWidgetState.LocationUnknown
        case .authorizedAlways, .authorizedWhenInUse:
            lat = locationManager.location?.coordinate.latitude ?? -1.0
            lng = locationManager.location?.coordinate.longitude ?? -1.0
        @unknown default:
            lat = -1.0
            lng = -1.0
        }
        
        if lat == -1.0 || lng == -1.0 {
            let entry = GoingHomeBusEntry(
                date: date,
                state: state
            )
            
            let timeline = Timeline(
                entries: [entry],
                policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
            )
            
            completion(timeline)
            
            return
        }
        
        Di.get()
            .getNearbyGoingHomeBusesUseCase()
            .invoke(
                lat: lat,
                lng: lng,
                onStart: { _ in }
            ) { result in
                let useCaseResult = Util.toUseCaseResult(result)
                switch useCaseResult {
                case .Error(let message):
                    let state = GoingHomeBusWidgetState.Error(message: message)
                    
                    let date = Date()
                    let entry = GoingHomeBusEntry(
                        date: date,
                        state: state
                    )
                    
                    let timeline = Timeline(
                        entries: [entry],
                        policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
                    )
                    
                    completion(timeline)
                case .Success(let data):
                    if data is GoingHomeBusResult.NoBusStopsNearby {
                        let state = GoingHomeBusWidgetState.NoBusStopsNearby
                        
                        let date = Date()
                        let entry = GoingHomeBusEntry(
                            date: date,
                            state: state
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
                        )
                        
                        completion(timeline)
                    }
                    
                    if data is GoingHomeBusResult.HomeBusStopNotSet {
                        let state = GoingHomeBusWidgetState.HomeBusStopNotSet
                        
                        let date = Date()
                        let entry = GoingHomeBusEntry(
                            date: date,
                            state: state
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
                        )
                        
                        completion(timeline)
                    }
                    
                    if let data = data as? GoingHomeBusResult.NoBusesGoingHome {
                        let state = GoingHomeBusWidgetState.NoBusesGoingHome(
                            homeBusStopDescription: data.homeBusStopDescription
                        )
                        
                        let date = Date()
                        let entry = GoingHomeBusEntry(
                            date: date,
                            state: state
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
                        )
                        
                        completion(timeline)
                    }
                    
                    if let data = data as? GoingHomeBusResult.TooCloseToHome {
                        let state = GoingHomeBusWidgetState.TooCloseToHome(
                            homeBusStopDescription: data.homeBusStopDescription
                        )
                        
                        let date = Date()
                        let entry = GoingHomeBusEntry(
                            date: date,
                            state: state
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
                        )
                        
                        completion(timeline)
                    }
                    
                    if let data = data as? GoingHomeBusResult.Success {
                        let closestDirectBus = data.directBuses[0]
                        let state = GoingHomeBusWidgetState.Success(
                            busServiceNumber: closestDirectBus.busServiceNumber,
                            sourceBusStopDescription: closestDirectBus.sourceBusStopDescription,
                            destinationBusStopDescription: closestDirectBus.destinationBusStopDescription,
                            stops: Int(closestDirectBus.stops),
                            distance: Double(closestDirectBus.distance)
                        )
                        
                        let date = Date()
                        let entry = GoingHomeBusEntry(
                            date: date,
                            state: state
                        )
                        
                        let timeline = Timeline(
                            entries: [entry],
                            policy: .after(Calendar.current.date(byAdding: .minute, value: 30, to: date)!)
                        )
                        
                        completion(timeline)
                    }
                }
            }
    }
}

@main
struct GoingHomeBusWidget: Widget {
    let kind: String = "io.github.amanshuraikwar.NxtBuz.goingHomeBusWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            GoingHomeBusWidgetEntryView(entry: entry)
        }
        .supportedFamilies([.systemSmall])
        .configurationDisplayName("Bus Going Home")
        .description("See if there is a  bus nearby which goes to your home")
    }
}

//struct GoingHomeBusWidget_Previews: PreviewProvider {
//    static var previews: some View {
//        GoingHomeBusWidgetEntryView(entry: SimpleEntry(date: Date()))
//            .previewContext(WidgetPreviewContext(family: .systemSmall))
//    }
//}
